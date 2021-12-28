package com.pyamsoft.trickle.main;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import javax.annotation.processing.Generated;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class MutableMainViewState_Factory implements Factory<MutableMainViewState> {
  @Override
  public MutableMainViewState get() {
    return newInstance();
  }

  public static MutableMainViewState_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MutableMainViewState newInstance() {
    return new MutableMainViewState();
  }

  private static final class InstanceHolder {
    private static final MutableMainViewState_Factory INSTANCE = new MutableMainViewState_Factory();
  }
}
