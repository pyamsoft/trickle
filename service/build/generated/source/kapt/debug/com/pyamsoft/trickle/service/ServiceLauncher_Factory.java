package com.pyamsoft.trickle.service;

import com.pyamsoft.pydroid.notify.Notifier;
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
public final class ServiceLauncher_Factory implements Factory<ServiceLauncher> {
  private final Provider<Notifier> notifierProvider;

  public ServiceLauncher_Factory(Provider<Notifier> notifierProvider) {
    this.notifierProvider = notifierProvider;
  }

  @Override
  public ServiceLauncher get() {
    return newInstance(notifierProvider.get());
  }

  public static ServiceLauncher_Factory create(Provider<Notifier> notifierProvider) {
    return new ServiceLauncher_Factory(notifierProvider);
  }

  public static ServiceLauncher newInstance(Notifier notifier) {
    return new ServiceLauncher(notifier);
  }
}
