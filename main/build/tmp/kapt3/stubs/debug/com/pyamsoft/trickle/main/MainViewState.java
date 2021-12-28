package com.pyamsoft.trickle.main;

import com.pyamsoft.pydroid.arch.UiViewState;
import com.pyamsoft.pydroid.ui.theme.Theming;
import com.pyamsoft.trickle.core.ActivityScope;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001R\u0012\u0010\u0002\u001a\u00020\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005\u00a8\u0006\u0006"}, d2 = {"Lcom/pyamsoft/trickle/main/MainViewState;", "Lcom/pyamsoft/pydroid/arch/UiViewState;", "theme", "Lcom/pyamsoft/pydroid/ui/theme/Theming$Mode;", "getTheme", "()Lcom/pyamsoft/pydroid/ui/theme/Theming$Mode;", "main_debug"})
public abstract interface MainViewState extends com.pyamsoft.pydroid.arch.UiViewState {
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.pyamsoft.pydroid.ui.theme.Theming.Mode getTheme();
}