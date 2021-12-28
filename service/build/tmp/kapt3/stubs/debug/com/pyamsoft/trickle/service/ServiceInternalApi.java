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

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\n\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0000\b\u0081\u0002\u0018\u00002\u00020\u0001B\u0000\u00a8\u0006\u0002"}, d2 = {"Lcom/pyamsoft/trickle/service/ServiceInternalApi;", "", "service_debug"})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.CLASS)
@kotlin.annotation.Retention(value = kotlin.annotation.AnnotationRetention.BINARY)
@javax.inject.Qualifier()
public abstract @interface ServiceInternalApi {
}