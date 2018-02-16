package edu.mit.media.mysnapshot.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import edu.mit.media.mysnapshot.R;
import edu.mit.media.mysnapshot.backend.BackendAPI;
import edu.mit.media.mysnapshot.backend.BackendAPIFactory;
import edu.mit.media.mysnapshot.backend.Experiment;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class HistoryActivity extends AppCompatActivity {

    public static final String LOGTAG = HistoryActivity.class.getName();

    SwipeRefreshLayout swiper;
    ListView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);

        list = (ListView) findViewById(R.id.list);
        list.setAdapter(new ExperimentAdapter(this));
        list.setEmptyView(findViewById(R.id.empty));
        SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadHistory();
            }
        };

        swiper = (SwipeRefreshLayout) findViewById(R.id.swiper);
        swiper.setOnRefreshListener(listener);

        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (listView == null || listView.getChildCount() == 0) ?
                        0 : listView.getChildAt(0).getTop();
                swiper.setEnabled((topRowVerticalPosition >= 0));
            }
        });
        loadHistory();

    }

    public void loadHistory() {
        setRefreshing(true);
        BackendAPIFactory.getAPI(this).getExperiments()
        .enqueue(new Callback<BackendAPI.ExperimentResults>() {

            @Override
            public void onResponse(Response<BackendAPI.ExperimentResults> response, Retrofit retrofit) {

                if (response.isSuccess() && response.body() != null && response.body().success) {

                    onLoadSuccess(response.body());

                } else {
                    onFailure(null);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "An error happened! Please swipe down to try again!", Toast.LENGTH_LONG).show();
                ((ExperimentAdapter)list.getAdapter()).clear();
                setRefreshing(false);
            }
        });
    }


    void onLoadSuccess(BackendAPI.ExperimentResults result) {

        ((ExperimentAdapter)list.getAdapter()).setItems(result.experiments);
        setRefreshing(false);
    }


    public class ExperimentAdapter extends ArrayAdapter<BackendAPI.ExperimentResults.ExperimentResult> {

        public void setItems(List<BackendAPI.ExperimentResults.ExperimentResult> items) {
            clear();
            addAll(items);
        }

        public ExperimentAdapter(Context context) {
            super(context, R.layout.view_history_experiment);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final BackendAPI.ExperimentResults.ExperimentResult experiment = getItem(position);
            Experiment.ExperimentType experimentType = Experiment.ExperimentType.getType(experiment.type);

            View root = convertView;

            if (root == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                root = vi.inflate(R.layout.view_history_experiment, null);
            }

            View background = root.findViewById(R.id.background);

            ImageView icon = (ImageView)root.findViewById(R.id.icon);
            icon.setImageResource(experimentType.iconId);

            TextView title = (TextView)root.findViewById(R.id.title);
            title.setText(experimentType.name);

            TextView dayCount = (TextView)root.findViewById(R.id.day_count);
            dayCount.setText(experiment.days + " Days" + (experiment.isCancelled ? " (Canceled)" : "") + (experiment.isActive ? " (Active)" : ""));

            TextView result = (TextView)root.findViewById(R.id.result);
            result.setText(experimentType.formatInstruction(experiment.resultValue));

            TextView confidence = (TextView)root.findViewById(R.id.confidence);
            confidence.setText(Integer.toString(Math.round(experiment.resultConfidence * 100f)) + "%");

            ViewGroup content = (ViewGroup) root.findViewById(R.id.content);
            for (int i = 0; i < content.getChildCount(); i++) {
                content.getChildAt(i).setVisibility(View.GONE);
            }

            final View cancelButton = root.findViewById(R.id.cancel_button);
            if (experiment.isCancelled) {
                background.setBackgroundColor(getResources().getColor(R.color.pageindicator_disabled));
                root.findViewById(R.id.canceled).setVisibility(View.VISIBLE);

                cancelButton.setOnClickListener(null);
            } else if (experiment.isActive) {
                background.setBackgroundColor(getResources().getColor(R.color.fadered));
                root.findViewById(R.id.in_progress).setVisibility(View.VISIBLE);

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelExperiment(experiment);
                    }
                });
            } else {
                background.setBackgroundColor(getResources().getColor(R.color.white));
                root.findViewById(R.id.finished).setVisibility(View.VISIBLE);

                cancelButton.setOnClickListener(null);
            }

            return root;
        }
    }

    protected void setRefreshing(final boolean refreshing) {
        swiper.post(new Runnable() {
            @Override
            public void run() {
                swiper.setRefreshing(refreshing);
            }
        });
    }

    AlertDialog dialog;

    protected void cancelExperiment(final BackendAPI.ExperimentResults.ExperimentResult experiment) {

        final EditText edittext = new EditText(this);
        edittext.setSingleLine();
        edittext.setHint("Reason to Quit");
        edittext.setImeOptions(EditorInfo.IME_ACTION_GO);

        edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    if (edittext.getText().toString().length() > 0) {
                        cancelConfirmed(experiment, edittext.getText().toString());
                        handled = true;
                        dialog.hide();
                    }
                }
                return handled;
            }
        });

        dialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Really Stop Experiment?")
                .setView(edittext)
                .setMessage("All your progress will be lost forever! If you want to quit, please let us know why.")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (edittext.getText().toString().length() > 0) {
                            cancelConfirmed(experiment, edittext.getText().toString());
                        } else {
                            Toast.makeText(HistoryActivity.this, "Please enter a reason", Toast.LENGTH_LONG).show();
                        }
                    }

                })
                .setNegativeButton("Cancel!", null).show();

    }

    public void cancelConfirmed(final BackendAPI.ExperimentResults.ExperimentResult experiment, String reason) {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_menu_delete)
                .setCancelable(false)
                .setTitle("Stopping Experiment")
                .show();

        BackendAPIFactory.getAPI(this).cancelExperiment(experiment.key, reason)
                .enqueue(new Callback<BackendAPI.ExperimentResults>() {

                    @Override
                    public void onResponse(Response<BackendAPI.ExperimentResults> response, Retrofit retrofit) {

                        dialog.dismiss();

                        if (response.isSuccess() && response.body() != null && response.body().success) {

                            onCancelSuccess(response.body());

                        } else {
                            onFailure(null);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        dialog.dismiss();

                        Toast.makeText(getApplicationContext(), "An error happened! Please try again!", Toast.LENGTH_LONG).show();
                        ((ExperimentAdapter)list.getAdapter()).clear();

                    }
                });
    }


    void onCancelSuccess(BackendAPI.ExperimentResults result) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Experiment.CURRENT_EXPERIMENT_PREF);
        editor.remove(Experiment.Checkin.LAST_CHECKIN_PREF);
        editor.apply();


        ((ExperimentAdapter)list.getAdapter()).setItems(result.experiments);
        setRefreshing(false);
    }

}