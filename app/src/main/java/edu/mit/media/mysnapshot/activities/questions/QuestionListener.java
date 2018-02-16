package edu.mit.media.mysnapshot.activities.questions;

public abstract class QuestionListener<T> {

    public abstract void onSelected(T value);

    public void onDataSave(T value) {

    }

    public void onResetQuestion() {

    }
}
