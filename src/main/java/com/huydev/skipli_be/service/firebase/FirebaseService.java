package com.huydev.skipli_be.service.firebase;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.huydev.skipli_be.entity.Verification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class FirebaseService {
    private static final String USERS_COLLECTION = "users";
    private static final String VERIFICATION_COLLECTION = "verifications";

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public boolean userExists(String email) {
        try {
            Firestore db = getFirestore();
            return !db.collection(USERS_COLLECTION)
                    .whereEqualTo("email", email)
                    .whereEqualTo("verified", true)
                    .get()
                    .get()
                    .isEmpty();
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public String saveUser(String email, String verification) {
        try {
            Firestore db = getFirestore();
            String userId = UUID.randomUUID().toString();

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", userId);
            userData.put("email", email);
            userData.put("verified", true);
            userData.put("createdAt", LocalDateTime.now().toString());
            userData.put("updatedAt", LocalDateTime.now().toString());

            db.collection(USERS_COLLECTION).document(userId).set(userData).get();
            return userId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    public void saveVerification(Verification verification) {
        try {
            Firestore db = getFirestore();
            String verificationId = UUID.randomUUID().toString();

            Map<String, Object> verificationData = new HashMap<>();
            verificationData.put("id", verificationId);
            verificationData.put("email", verification.getEmail());
            verificationData.put("token", verification.getToken());
            verificationData.put("expiresAt", verification.getExpiresAt().toString());
            verificationData.put("verificationType", verification.getVerificationType().toString());
            verificationData.put("createdAt", LocalDateTime.now().toString());

            db.collection(VERIFICATION_COLLECTION).document(verificationId).set(verificationData).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save verification", e);
        }
    }

    public Verification getVerificationByEmailAndToken(String email, String token) {
        try {
            Firestore db = getFirestore();

            var verificationQuery = db.collection(VERIFICATION_COLLECTION)
                    .whereEqualTo("email", email)
                    .whereEqualTo("token", token)
                    .get()
                    .get();
            if(verificationQuery.isEmpty()) {
                return null;
            }

            QueryDocumentSnapshot doc = verificationQuery.getDocuments().get(0);
            return Verification.builder()
                    .id(doc.getString("id"))
                    .email(doc.getString("email"))
                    .token(doc.getString("token"))
                    .expiresAt(java.time.Instant.parse(doc.getString("expiredAt")))
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteVerificationByEmail(String email) {
        try {
            Firestore db = getFirestore();

            var verificationQuery = db.collection(VERIFICATION_COLLECTION)
                    .whereEqualTo("email", email)
                    .get()
                    .get();
            for (QueryDocumentSnapshot doc : verificationQuery) {
                doc.getReference().delete();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete verification", e);
        }
    }
}
