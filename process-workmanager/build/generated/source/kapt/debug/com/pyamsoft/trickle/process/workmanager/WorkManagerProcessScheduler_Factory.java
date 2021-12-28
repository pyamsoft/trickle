package com.pyamsoft.trickle.process.workmanager;

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
public final class WorkManagerProcessScheduler_Factory implements Factory<WorkManagerProcessScheduler> {
  private final Provider<Context> contextProvider;

  public WorkManagerProcessScheduler_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public WorkManagerProcessScheduler get() {
    return newInstance(contextProvider.get());
  }

  public static WorkManagerProcessScheduler_Factory create(Provider<Context> contextProvider) {
    return new WorkManagerProcessScheduler_Factory(contextProvider);
  }

  public static WorkManagerProcessScheduler newInstance(Context context) {
    return new WorkManagerProcessScheduler(context);
  }
}
