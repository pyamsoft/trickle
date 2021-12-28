package com.pyamsoft.trickle.main;

import android.app.Activity;
import com.pyamsoft.pydroid.arch.AbstractViewModeler;
import com.pyamsoft.pydroid.arch.UiSavedStateReader;
import com.pyamsoft.pydroid.arch.UiSavedStateWriter;
import com.pyamsoft.pydroid.ui.theme.Theming;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u00122\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0012B\u0017\b\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J\u000e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u0010\u0010\f\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u000eH\u0016J\u0010\u0010\u000f\u001a\u00020\t2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/pyamsoft/trickle/main/MainViewModeler;", "Lcom/pyamsoft/pydroid/arch/AbstractViewModeler;", "Lcom/pyamsoft/trickle/main/MainViewState;", "state", "Lcom/pyamsoft/trickle/main/MutableMainViewState;", "theming", "Lcom/pyamsoft/pydroid/ui/theme/Theming;", "(Lcom/pyamsoft/trickle/main/MutableMainViewState;Lcom/pyamsoft/pydroid/ui/theme/Theming;)V", "handleSyncDarkTheme", "", "activity", "Landroid/app/Activity;", "restoreState", "savedInstanceState", "Lcom/pyamsoft/pydroid/arch/UiSavedStateReader;", "saveState", "outState", "Lcom/pyamsoft/pydroid/arch/UiSavedStateWriter;", "Companion", "main_debug"})
public final class MainViewModeler extends com.pyamsoft.pydroid.arch.AbstractViewModeler<com.pyamsoft.trickle.main.MainViewState> {
    private final com.pyamsoft.trickle.main.MutableMainViewState state = null;
    private final com.pyamsoft.pydroid.ui.theme.Theming theming = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.pyamsoft.trickle.main.MainViewModeler.Companion Companion = null;
    private static final java.lang.String KEY_THEME = "theme";
    
    @javax.inject.Inject()
    public MainViewModeler(@org.jetbrains.annotations.NotNull()
    com.pyamsoft.trickle.main.MutableMainViewState state, @org.jetbrains.annotations.NotNull()
    com.pyamsoft.pydroid.ui.theme.Theming theming) {
        super(null);
    }
    
    public final void handleSyncDarkTheme(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity) {
    }
    
    @java.lang.Override()
    public void saveState(@org.jetbrains.annotations.NotNull()
    com.pyamsoft.pydroid.arch.UiSavedStateWriter outState) {
    }
    
    @java.lang.Override()
    public void restoreState(@org.jetbrains.annotations.NotNull()
    com.pyamsoft.pydroid.arch.UiSavedStateReader savedInstanceState) {
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/pyamsoft/trickle/main/MainViewModeler$Companion;", "", "()V", "KEY_THEME", "", "main_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}