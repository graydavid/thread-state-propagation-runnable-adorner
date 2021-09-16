/*
 * Copyright 2021 David Gray
 * 
 * SPDX-License-Identifier: Apache-2.0
 */

package io.github.graydavid.threadstatepropagationrunnableadorner;

import java.util.Objects;

import io.github.graydavid.runnabledecoratingexecutor.AfterRunnableAction;
import io.github.graydavid.runnabledecoratingexecutor.BeforeRunnableAction;
import io.github.graydavid.runnabledecoratingexecutor.RunnableAdorner;
import io.github.graydavid.threadstatepropagation.ThreadStateBindable;
import io.github.graydavid.threadstatepropagation.ThreadStatePropagationStarter;
import io.github.graydavid.threadstatepropagation.ThreadStateRestorable;

/**
 * Adapts ThreadStatePropagationStarter into RunnableAdorner. This is useful for propagating thread state as part of
 * running an Executor (one of the most common ways of jumping threads). ThreadStatePropagationStarter is run as part of
 * the RunnableAdorner (i.e. get the thread state from the thread invoking the executor), ThreadStateBindable is run as
 * part of the BeforeRunnableAction (i.e. save the restorable-state of and propagate the invoking-thread state to the
 * running thread, as chosen by the Executor) , and ThreadStateRestorable is run as part of the AfterRunnableAction
 * (i.e. restore the restorable-state of the running thread before finishing).
 */
public class ThreadStatePropagationRunnableAdorner implements RunnableAdorner {
    private final ThreadStatePropagationStarter starter;

    private ThreadStatePropagationRunnableAdorner(ThreadStatePropagationStarter starter) {
        this.starter = Objects.requireNonNull(starter);
    }

    public static RunnableAdorner from(ThreadStatePropagationStarter starter) {
        return new ThreadStatePropagationRunnableAdorner(starter);
    }

    @Override
    public BeforeRunnableAction createAdornment(Runnable runnable) {
        ThreadStateBindable bindable = starter.createBindableFromCurrentThread();
        return newBindableBeforeAction(bindable);
    }

    private static BeforeRunnableAction newBindableBeforeAction(ThreadStateBindable bindable) {
        return () -> {
            ThreadStateRestorable restorable = bindable.bindToCurrentThread();
            return newRestorableAfterAction(restorable);
        };
    }

    private static AfterRunnableAction newRestorableAfterAction(ThreadStateRestorable restorable) {
        return throwable -> restorable.restoreToCurrentThread();
    }
}
