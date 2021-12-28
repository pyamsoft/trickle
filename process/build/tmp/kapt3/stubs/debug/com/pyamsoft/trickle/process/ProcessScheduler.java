package com.pyamsoft.trickle.process;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0011\u0010\u0002\u001a\u00020\u0003H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0004J\u0019\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\b\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\t"}, d2 = {"Lcom/pyamsoft/trickle/process/ProcessScheduler;", "", "cancel", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "schedulePowerSaving", "enable", "", "(ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "process_debug"})
public abstract interface ProcessScheduler {
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object cancel(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> continuation);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object schedulePowerSaving(boolean enable, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> continuation);
}