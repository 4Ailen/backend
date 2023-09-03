package com.aliens.friendship.global.config.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import org.springframework.stereotype.Component;

@Component
public class FirebaseMessagingWrapperImpl implements FirebaseMessagingWrapper {
    @Override
    public void sendAsync(Message message) {
        FirebaseMessaging.getInstance().sendAsync(message);
    }

    @Override
    public void sendMulticast(MulticastMessage message) throws FirebaseMessagingException {
        FirebaseMessaging.getInstance().sendMulticast(message);
    }
}
