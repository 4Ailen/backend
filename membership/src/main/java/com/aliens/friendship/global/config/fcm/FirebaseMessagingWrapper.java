package com.aliens.friendship.global.config.fcm;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;

public interface FirebaseMessagingWrapper {
    void sendAsync(Message message);

    void sendMulticast(MulticastMessage message) throws FirebaseMessagingException;
}
