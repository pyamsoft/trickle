package com.pyamsoft.trickle.main;

import android.os.Bundle;
import androidx.annotation.CheckResult;
import com.pyamsoft.pydroid.ui.navigator.Navigator;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0001\tB\u000f\b\u0004\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00000\bH\u0007R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u0082\u0001\u0001\n\u00a8\u0006\u000b"}, d2 = {"Lcom/pyamsoft/trickle/main/MainPage;", "", "name", "", "(Ljava/lang/String;)V", "getName", "()Ljava/lang/String;", "asScreen", "Lcom/pyamsoft/pydroid/ui/navigator/Navigator$Screen;", "Home", "Lcom/pyamsoft/trickle/main/MainPage$Home;", "main_debug"})
public abstract class MainPage {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String name = null;
    
    private MainPage(java.lang.String name) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @androidx.annotation.CheckResult()
    public final com.pyamsoft.pydroid.ui.navigator.Navigator.Screen<com.pyamsoft.trickle.main.MainPage> asScreen() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/pyamsoft/trickle/main/MainPage$Home;", "Lcom/pyamsoft/trickle/main/MainPage;", "()V", "main_debug"})
    public static final class Home extends com.pyamsoft.trickle.main.MainPage {
        @org.jetbrains.annotations.NotNull()
        public static final com.pyamsoft.trickle.main.MainPage.Home INSTANCE = null;
        
        private Home() {
            super(null);
        }
    }
}