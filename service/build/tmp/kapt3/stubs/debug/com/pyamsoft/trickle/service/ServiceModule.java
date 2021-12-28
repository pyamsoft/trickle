package com.pyamsoft.trickle.service;

import android.content.Context;
import androidx.annotation.CheckResult;
import com.pyamsoft.pydroid.notify.Notifier;
import com.pyamsoft.pydroid.notify.NotifyDispatcher;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import javax.inject.Qualifier;
import javax.inject.Singleton;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\'\u0018\u0000 \b2\u00020\u0001:\u0001\bB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0019\u0010\u0003\u001a\u0006\u0012\u0002\b\u00030\u00042\u0006\u0010\u0005\u001a\u00020\u0006H!\u00a2\u0006\u0002\b\u0007\u00a8\u0006\t"}, d2 = {"Lcom/pyamsoft/trickle/service/ServiceModule;", "", "()V", "bindDispatcher", "Lcom/pyamsoft/pydroid/notify/NotifyDispatcher;", "impl", "Lcom/pyamsoft/trickle/service/ServiceDispatcher;", "bindDispatcher$service_debug", "Companion", "service_debug"})
@dagger.Module()
public abstract class ServiceModule {
    @org.jetbrains.annotations.NotNull()
    public static final com.pyamsoft.trickle.service.ServiceModule.Companion Companion = null;
    
    public ServiceModule() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    @ServiceInternalApi()
    @dagger.multibindings.IntoSet()
    @dagger.Binds()
    public abstract com.pyamsoft.pydroid.notify.NotifyDispatcher<?> bindDispatcher$service_debug(@org.jetbrains.annotations.NotNull()
    com.pyamsoft.trickle.service.ServiceDispatcher impl);
    
    @org.jetbrains.annotations.NotNull()
    @ServiceInternalApi()
    @androidx.annotation.CheckResult()
    @kotlin.jvm.JvmStatic()
    @javax.inject.Singleton()
    @dagger.Provides()
    public static final com.pyamsoft.pydroid.notify.Notifier provideNotifier$service_debug(@org.jetbrains.annotations.NotNull()
    @ServiceInternalApi()
    java.util.Set<com.pyamsoft.pydroid.notify.NotifyDispatcher<?>> dispatchers, @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010#\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J)\u0010\u0003\u001a\u00020\u00042\u0012\b\u0001\u0010\u0005\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00070\u00062\u0006\u0010\b\u001a\u00020\tH\u0001\u00a2\u0006\u0002\b\n\u00a8\u0006\u000b"}, d2 = {"Lcom/pyamsoft/trickle/service/ServiceModule$Companion;", "", "()V", "provideNotifier", "Lcom/pyamsoft/pydroid/notify/Notifier;", "dispatchers", "", "Lcom/pyamsoft/pydroid/notify/NotifyDispatcher;", "context", "Landroid/content/Context;", "provideNotifier$service_debug", "service_debug"})
    @dagger.Module()
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        @ServiceInternalApi()
        @androidx.annotation.CheckResult()
        @kotlin.jvm.JvmStatic()
        @javax.inject.Singleton()
        @dagger.Provides()
        public final com.pyamsoft.pydroid.notify.Notifier provideNotifier$service_debug(@org.jetbrains.annotations.NotNull()
        @ServiceInternalApi()
        java.util.Set<com.pyamsoft.pydroid.notify.NotifyDispatcher<?>> dispatchers, @org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}