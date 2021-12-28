package com.pyamsoft.trickle.home;

import androidx.annotation.StringRes;
import androidx.compose.material.icons.Icons;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.tooling.preview.Preview;

@kotlin.Metadata(mv = {1, 5, 1}, k = 2, d1 = {"\u0000*\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\u001a&\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a \u0010\u0007\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0003\u001a*\u0010\n\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0001\u0010\u000b\u001a\u00020\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0003\u001aL\u0010\u000e\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0001\u0010\u000b\u001a\u00020\f2\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001a\b\u0010\u0010\u001a\u00020\u0001H\u0003\u00a8\u0006\u0011"}, d2 = {"AdbInstructions", "", "modifier", "Landroidx/compose/ui/Modifier;", "onCopy", "Lkotlin/Function1;", "", "GoToSettings", "onOpenSettings", "Lkotlin/Function0;", "Header", "appNameRes", "", "onOpenApplicationSettings", "HomeScreen", "onOpenBatterySettings", "PreviewHomeScreen", "home_debug"})
public final class HomeScreenKt {
    
    @kotlin.jvm.JvmOverloads()
    @androidx.compose.runtime.Composable()
    public static final void HomeScreen(@androidx.annotation.StringRes()
    int appNameRes, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onCopy, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onOpenBatterySettings, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onOpenApplicationSettings) {
    }
    
    @kotlin.jvm.JvmOverloads()
    @androidx.compose.runtime.Composable()
    public static final void HomeScreen(@org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @androidx.annotation.StringRes()
    int appNameRes, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onCopy, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onOpenBatterySettings, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onOpenApplicationSettings) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void Header(androidx.compose.ui.Modifier modifier, @androidx.annotation.StringRes()
    int appNameRes, kotlin.jvm.functions.Function0<kotlin.Unit> onOpenApplicationSettings) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AdbInstructions(androidx.compose.ui.Modifier modifier, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onCopy) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void GoToSettings(androidx.compose.ui.Modifier modifier, kotlin.jvm.functions.Function0<kotlin.Unit> onOpenSettings) {
    }
    
    @androidx.compose.runtime.Composable()
    @androidx.compose.ui.tooling.preview.Preview()
    private static final void PreviewHomeScreen() {
    }
}