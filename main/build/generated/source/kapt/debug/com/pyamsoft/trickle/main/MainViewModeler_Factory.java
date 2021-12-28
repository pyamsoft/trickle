package com.pyamsoft.trickle.main;

import com.pyamsoft.pydroid.ui.theme.Theming;
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
public final class MainViewModeler_Factory implements Factory<MainViewModeler> {
  private final Provider<MutableMainViewState> stateProvider;

  private final Provider<Theming> themingProvider;

  public MainViewModeler_Factory(Provider<MutableMainViewState> stateProvider,
      Provider<Theming> themingProvider) {
    this.stateProvider = stateProvider;
    this.themingProvider = themingProvider;
  }

  @Override
  public MainViewModeler get() {
    return newInstance(stateProvider.get(), themingProvider.get());
  }

  public static MainViewModeler_Factory create(Provider<MutableMainViewState> stateProvider,
      Provider<Theming> themingProvider) {
    return new MainViewModeler_Factory(stateProvider, themingProvider);
  }

  public static MainViewModeler newInstance(MutableMainViewState state, Theming theming) {
    return new MainViewModeler(state, theming);
  }
}
