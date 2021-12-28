package com.pyamsoft.trickle.service;

import android.app.Activity;
import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class ServiceDispatcher_Factory implements Factory<ServiceDispatcher> {
  private final Provider<Context> contextProvider;

  private final Provider<Integer> appNameResProvider;

  private final Provider<Integer> smallNotificationIconProvider;

  private final Provider<Class<? extends Activity>> activityClassProvider;

  public ServiceDispatcher_Factory(Provider<Context> contextProvider,
      Provider<Integer> appNameResProvider, Provider<Integer> smallNotificationIconProvider,
      Provider<Class<? extends Activity>> activityClassProvider) {
    this.contextProvider = contextProvider;
    this.appNameResProvider = appNameResProvider;
    this.smallNotificationIconProvider = smallNotificationIconProvider;
    this.activityClassProvider = activityClassProvider;
  }

  @Override
  public ServiceDispatcher get() {
    return newInstance(contextProvider.get(), appNameResProvider.get(), smallNotificationIconProvider.get(), activityClassProvider.get());
  }

  public static ServiceDispatcher_Factory create(Provider<Context> contextProvider,
      Provider<Integer> appNameResProvider, Provider<Integer> smallNotificationIconProvider,
      Provider<Class<? extends Activity>> activityClassProvider) {
    return new ServiceDispatcher_Factory(contextProvider, appNameResProvider, smallNotificationIconProvider, activityClassProvider);
  }

  public static ServiceDispatcher newInstance(Context context, int appNameRes,
      int smallNotificationIcon, Class<? extends Activity> activityClass) {
    return new ServiceDispatcher(context, appNameRes, smallNotificationIcon, activityClass);
  }
}
