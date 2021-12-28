package com.pyamsoft.trickle.process.workmanager;

import android.content.Context;
import androidx.annotation.CheckResult;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import com.google.common.util.concurrent.ListenableFuture;
import com.pyamsoft.pydroid.core.Enforcer;
import com.pyamsoft.trickle.process.ProcessScheduler;
import com.pyamsoft.trickle.process.workmanager.worker.PowerSavingWorker;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import kotlinx.coroutines.Dispatchers;
import timber.log.Timber;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u0000 \u00102\u00020\u0001:\u0001\u0010B\u000f\b\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0011\u0010\u0005\u001a\u00020\u0006H\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0007J\u0011\u0010\b\u001a\u00020\u0006H\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0007J\u0019\u0010\t\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u000bH\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\fJ\u0019\u0010\r\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u000bH\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\fJ\b\u0010\u000e\u001a\u00020\u000fH\u0003R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0011"}, d2 = {"Lcom/pyamsoft/trickle/process/workmanager/WorkManagerProcessScheduler;", "Lcom/pyamsoft/trickle/process/ProcessScheduler;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "cancel", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "cancelPowerSaving", "queuePowerSaving", "enable", "", "(ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "schedulePowerSaving", "workManager", "Landroidx/work/WorkManager;", "Companion", "process-workmanager_debug"})
@javax.inject.Singleton()
public final class WorkManagerProcessScheduler implements com.pyamsoft.trickle.process.ProcessScheduler {
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.pyamsoft.trickle.process.workmanager.WorkManagerProcessScheduler.Companion Companion = null;
    private static final java.lang.String POWER_SAVING_TAG = "power_saving_tag";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_ENABLE = "key_enable";
    private static final java.util.concurrent.Executor alerterExecutor = null;
    
    @javax.inject.Inject()
    public WorkManagerProcessScheduler(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @androidx.annotation.CheckResult()
    private final androidx.work.WorkManager workManager() {
        return null;
    }
    
    private final java.lang.Object queuePowerSaving(boolean enable, kotlin.coroutines.Continuation<? super kotlin.Unit> continuation) {
        return null;
    }
    
    private final java.lang.Object cancelPowerSaving(kotlin.coroutines.Continuation<? super kotlin.Unit> continuation) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Object cancel(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> continuation) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Object schedulePowerSaving(boolean enable, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> continuation) {
        return null;
    }
    
    @androidx.annotation.CheckResult()
    @kotlin.jvm.JvmStatic()
    private static final androidx.work.Data createInputData(boolean enable) {
        return null;
    }
    
    @androidx.annotation.CheckResult()
    @kotlin.jvm.JvmStatic()
    private static final androidx.work.WorkRequest createWork(java.lang.Class<? extends androidx.work.Worker> work, java.lang.String tag, androidx.work.Data inputData) {
        return null;
    }
    
    @androidx.annotation.CheckResult()
    @kotlin.jvm.JvmStatic()
    private static final java.lang.Class<? extends androidx.work.Worker> getWorkClass() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0003J(\u0010\f\u001a\u00020\r2\u000e\u0010\u000e\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00100\u000f2\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\tH\u0003J\u0010\u0010\u0013\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00100\u000fH\u0003J\u0015\u0010\u0014\u001a\u00020\u0015*\u00020\u0016H\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0017J!\u0010\u0014\u001a\u0002H\u0018\"\u0004\b\u0000\u0010\u0018*\b\u0012\u0004\u0012\u0002H\u00180\u0019H\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u001aR\u000e\u0010\u0003\u001a\u00020\u0004X\u0080T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u001b"}, d2 = {"Lcom/pyamsoft/trickle/process/workmanager/WorkManagerProcessScheduler$Companion;", "", "()V", "KEY_ENABLE", "", "POWER_SAVING_TAG", "alerterExecutor", "Ljava/util/concurrent/Executor;", "createInputData", "Landroidx/work/Data;", "enable", "", "createWork", "Landroidx/work/WorkRequest;", "work", "Ljava/lang/Class;", "Landroidx/work/Worker;", "tag", "inputData", "getWorkClass", "await", "", "Landroidx/work/Operation;", "(Landroidx/work/Operation;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "R", "Lcom/google/common/util/concurrent/ListenableFuture;", "(Lcom/google/common/util/concurrent/ListenableFuture;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "process-workmanager_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        private final java.lang.Object await(androidx.work.Operation $this$await, kotlin.coroutines.Continuation<? super kotlin.Unit> p1) {
            return null;
        }
        
        @kotlin.Suppress(names = {"BlockingMethodInNonBlockingContext"})
        private final <R extends java.lang.Object>java.lang.Object await(com.google.common.util.concurrent.ListenableFuture<R> $this$await, kotlin.coroutines.Continuation<? super R> p1) {
            return null;
        }
        
        @androidx.annotation.CheckResult()
        @kotlin.jvm.JvmStatic()
        private final androidx.work.Data createInputData(boolean enable) {
            return null;
        }
        
        @androidx.annotation.CheckResult()
        @kotlin.jvm.JvmStatic()
        private final androidx.work.WorkRequest createWork(java.lang.Class<? extends androidx.work.Worker> work, java.lang.String tag, androidx.work.Data inputData) {
            return null;
        }
        
        @androidx.annotation.CheckResult()
        @kotlin.jvm.JvmStatic()
        private final java.lang.Class<? extends androidx.work.Worker> getWorkClass() {
            return null;
        }
    }
}