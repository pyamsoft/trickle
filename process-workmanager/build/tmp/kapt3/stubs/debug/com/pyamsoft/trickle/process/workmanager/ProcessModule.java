package com.pyamsoft.trickle.process.workmanager;

import androidx.annotation.CheckResult;
import com.pyamsoft.trickle.process.ProcessScheduler;
import dagger.Binds;
import dagger.Module;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0015\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H!\u00a2\u0006\u0002\b\u0007\u00a8\u0006\b"}, d2 = {"Lcom/pyamsoft/trickle/process/workmanager/ProcessModule;", "", "()V", "bindProcessScheduler", "Lcom/pyamsoft/trickle/process/ProcessScheduler;", "impl", "Lcom/pyamsoft/trickle/process/workmanager/WorkManagerProcessScheduler;", "bindProcessScheduler$process_workmanager_debug", "process-workmanager_debug"})
@dagger.Module()
public abstract class ProcessModule {
    
    public ProcessModule() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    @androidx.annotation.CheckResult()
    @dagger.Binds()
    public abstract com.pyamsoft.trickle.process.ProcessScheduler bindProcessScheduler$process_workmanager_debug(@org.jetbrains.annotations.NotNull()
    com.pyamsoft.trickle.process.workmanager.WorkManagerProcessScheduler impl);
}