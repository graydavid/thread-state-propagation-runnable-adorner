package io.github.graydavid.threadstatepropagationrunnableadorner;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import io.github.graydavid.runnabledecoratingexecutor.AfterRunnableAction;
import io.github.graydavid.runnabledecoratingexecutor.BeforeRunnableAction;
import io.github.graydavid.runnabledecoratingexecutor.RunnableAdorner;
import io.github.graydavid.threadstatepropagation.ThreadStateBindable;
import io.github.graydavid.threadstatepropagation.ThreadStatePropagationStarter;
import io.github.graydavid.threadstatepropagation.ThreadStateRestorable;

public class ThreadStatePropagationRunnableAdapterTest {

    @Test
    public void constructorThrowsExceptionGivenNullArguments() {
        assertThrows(NullPointerException.class, () -> ThreadStatePropagationRunnableAdorner.from(null));
    }

    @Test
    public void runsStartBindRestoreAsPartOfAdornBeforeAfterCycle() {
        ThreadStatePropagationStarter starter = mock(ThreadStatePropagationStarter.class);
        ThreadStateBindable bindable = mock(ThreadStateBindable.class);
        ThreadStateRestorable restorable = mock(ThreadStateRestorable.class);
        when(starter.createBindableFromCurrentThread()).thenReturn(bindable);
        when(bindable.bindToCurrentThread()).thenReturn(restorable);
        RunnableAdorner adorner = ThreadStatePropagationRunnableAdorner.from(starter);

        verifyNoInteractions(starter, bindable, restorable);
        BeforeRunnableAction beforeAction = adorner.createAdornment(mock(Runnable.class));

        verify(starter).createBindableFromCurrentThread();
        verifyNoInteractions(bindable, restorable);
        AfterRunnableAction afterAction = beforeAction.runBeforeRunnable();

        verify(bindable).bindToCurrentThread();
        verifyNoInteractions(restorable);
        afterAction.runAfterRunnable(null);

        verify(restorable).restoreToCurrentThread();
    }
}
