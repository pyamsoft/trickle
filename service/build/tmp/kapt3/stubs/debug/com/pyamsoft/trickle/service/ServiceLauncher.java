package com.pyamsoft.trickle.service;

import android.app.Service;
import com.pyamsoft.pydroid.notify.Notifier;
import com.pyamsoft.pydroid.notify.NotifyChannelInfo;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u0000 \n2\u00020\u0001:\u0001\nB\u0011\b\u0001\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u000e\u0010\t\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/pyamsoft/trickle/service/ServiceLauncher;", "", "notifier", "Lcom/pyamsoft/pydroid/notify/Notifier;", "(Lcom/pyamsoft/pydroid/notify/Notifier;)V", "createNotification", "", "service", "Landroid/app/Service;", "stopNotification", "Companion", "service_debug"})
@javax.inject.Singleton()
public final class ServiceLauncher {
    private final com.pyamsoft.pydroid.notify.Notifier notifier = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.pyamsoft.trickle.service.ServiceLauncher.Companion Companion = null;
    private static final com.pyamsoft.pydroid.notify.NotifyId NOTIFICATION_ID = null;
    private static final com.pyamsoft.pydroid.notify.NotifyChannelInfo CHANNEL_INFO = null;
    
    @javax.inject.Inject()
    public ServiceLauncher(@org.jetbrains.annotations.NotNull()
    @ServiceInternalApi()
    com.pyamsoft.pydroid.notify.Notifier notifier) {
        super();
    }
    
    public final void createNotification(@org.jetbrains.annotations.NotNull()
    android.app.Service service) {
    }
    
    public final void stopNotification(@org.jetbrains.annotations.NotNull()
    android.app.Service service) {
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/pyamsoft/trickle/service/ServiceLauncher$Companion;", "", "()V", "CHANNEL_INFO", "Lcom/pyamsoft/pydroid/notify/NotifyChannelInfo;", "NOTIFICATION_ID", "Lcom/pyamsoft/pydroid/notify/NotifyId;", "service_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}