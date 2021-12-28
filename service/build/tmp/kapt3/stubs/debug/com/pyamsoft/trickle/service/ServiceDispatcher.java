package com.pyamsoft.trickle.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.CheckResult;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import com.pyamsoft.pydroid.notify.NotifyChannelInfo;
import com.pyamsoft.pydroid.notify.NotifyData;
import com.pyamsoft.pydroid.notify.NotifyDispatcher;
import com.pyamsoft.pydroid.notify.NotifyId;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import timber.log.Timber;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\b\u0001\u0018\u0000 #2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0002#$B3\b\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\b\b\u0001\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0001\u0010\u0007\u001a\u00020\u0006\u0012\u000e\u0010\b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\n0\t\u00a2\u0006\u0002\u0010\u000bJ \u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0002H\u0016J\u0010\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u0018\u001a\u00020\u001bH\u0016J\u0010\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u0016\u001a\u00020\u0017H\u0003J\b\u0010\u001e\u001a\u00020\u001fH\u0003J\u0010\u0010 \u001a\u00020!2\u0006\u0010\u0016\u001a\u00020\u0017H\u0002J\u0010\u0010\"\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u0017H\u0003R\u0016\u0010\b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001b\u0010\f\u001a\u00020\r8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0010\u0010\u0011\u001a\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006%"}, d2 = {"Lcom/pyamsoft/trickle/service/ServiceDispatcher;", "Lcom/pyamsoft/pydroid/notify/NotifyDispatcher;", "Lcom/pyamsoft/trickle/service/ServiceDispatcher$Data;", "context", "Landroid/content/Context;", "appNameRes", "", "smallNotificationIcon", "activityClass", "Ljava/lang/Class;", "Landroid/app/Activity;", "(Landroid/content/Context;IILjava/lang/Class;)V", "channelCreator", "Landroid/app/NotificationManager;", "getChannelCreator", "()Landroid/app/NotificationManager;", "channelCreator$delegate", "Lkotlin/Lazy;", "build", "Landroid/app/Notification;", "id", "Lcom/pyamsoft/pydroid/notify/NotifyId;", "channelInfo", "Lcom/pyamsoft/pydroid/notify/NotifyChannelInfo;", "notification", "canShow", "", "Lcom/pyamsoft/pydroid/notify/NotifyData;", "createNotificationBuilder", "Landroidx/core/app/NotificationCompat$Builder;", "getActivityPendingIntent", "Landroid/app/PendingIntent;", "guaranteeNotificationChannelExists", "", "hydrateNotification", "Companion", "Data", "service_debug"})
@javax.inject.Singleton()
public final class ServiceDispatcher implements com.pyamsoft.pydroid.notify.NotifyDispatcher<com.pyamsoft.trickle.service.ServiceDispatcher.Data> {
    private final android.content.Context context = null;
    private final int appNameRes = 0;
    private final int smallNotificationIcon = 0;
    private final java.lang.Class<? extends android.app.Activity> activityClass = null;
    private final kotlin.Lazy channelCreator$delegate = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.pyamsoft.trickle.service.ServiceDispatcher.Companion Companion = null;
    private static final int REQUEST_CODE_ACTIVITY = 1337420;
    
    @javax.inject.Inject()
    public ServiceDispatcher(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @androidx.annotation.StringRes()
    @javax.inject.Named(value = "app_name")
    int appNameRes, @androidx.annotation.DrawableRes()
    @javax.inject.Named(value = "app_icon")
    int smallNotificationIcon, @org.jetbrains.annotations.NotNull()
    java.lang.Class<? extends android.app.Activity> activityClass) {
        super();
    }
    
    private final android.app.NotificationManager getChannelCreator() {
        return null;
    }
    
    private final void guaranteeNotificationChannelExists(com.pyamsoft.pydroid.notify.NotifyChannelInfo channelInfo) {
    }
    
    @androidx.annotation.CheckResult()
    private final android.app.PendingIntent getActivityPendingIntent() {
        return null;
    }
    
    @androidx.annotation.CheckResult()
    private final androidx.core.app.NotificationCompat.Builder createNotificationBuilder(com.pyamsoft.pydroid.notify.NotifyChannelInfo channelInfo) {
        return null;
    }
    
    @androidx.annotation.CheckResult()
    private final android.app.Notification hydrateNotification(com.pyamsoft.pydroid.notify.NotifyChannelInfo channelInfo) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public android.app.Notification build(@org.jetbrains.annotations.NotNull()
    com.pyamsoft.pydroid.notify.NotifyId id, @org.jetbrains.annotations.NotNull()
    com.pyamsoft.pydroid.notify.NotifyChannelInfo channelInfo, @org.jetbrains.annotations.NotNull()
    com.pyamsoft.trickle.service.ServiceDispatcher.Data notification) {
        return null;
    }
    
    @java.lang.Override()
    public boolean canShow(@org.jetbrains.annotations.NotNull()
    com.pyamsoft.pydroid.notify.NotifyData notification) {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/pyamsoft/trickle/service/ServiceDispatcher$Data;", "Lcom/pyamsoft/pydroid/notify/NotifyData;", "()V", "service_debug"})
    public static final class Data implements com.pyamsoft.pydroid.notify.NotifyData {
        @org.jetbrains.annotations.NotNull()
        public static final com.pyamsoft.trickle.service.ServiceDispatcher.Data INSTANCE = null;
        
        private Data() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/pyamsoft/trickle/service/ServiceDispatcher$Companion;", "", "()V", "REQUEST_CODE_ACTIVITY", "", "service_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}