package com.pyamsoft.trickle.service;

import android.content.Context;
import com.pyamsoft.pydroid.notify.Notifier;
import com.pyamsoft.pydroid.notify.NotifyDispatcher;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.Set;
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
public final class ServiceModule_Companion_ProvideNotifier$service_debugFactory implements Factory<Notifier> {
  private final Provider<Set<NotifyDispatcher<?>>> dispatchersProvider;

  private final Provider<Context> contextProvider;

  public ServiceModule_Companion_ProvideNotifier$service_debugFactory(
      Provider<Set<NotifyDispatcher<?>>> dispatchersProvider, Provider<Context> contextProvider) {
    this.dispatchersProvider = dispatchersProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public Notifier get() {
    return provideNotifier$service_debug(dispatchersProvider.get(), contextProvider.get());
  }

  public static ServiceModule_Companion_ProvideNotifier$service_debugFactory create(
      Provider<Set<NotifyDispatcher<?>>> dispatchersProvider, Provider<Context> contextProvider) {
    return new ServiceModule_Companion_ProvideNotifier$service_debugFactory(dispatchersProvider, contextProvider);
  }

  public static Notifier provideNotifier$service_debug(Set<NotifyDispatcher<?>> dispatchers,
      Context context) {
    return Preconditions.checkNotNullFromProvides(ServiceModule.Companion.provideNotifier$service_debug(dispatchers, context));
  }
}
