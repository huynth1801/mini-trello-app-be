package com.huydev.skipli_be.service.firebase;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.huydev.skipli_be.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class FirebaseService {
    private static final String USERS_COLLECTION = "users";
    private static final String VERIFICATION_COLLECTION = "verifications";
    private static final String CARD_COLLECTION = "cards";
    private static final String TASKS_COLLECTION = "tasks";

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

    public Users getUserByEmail(String email) {
        try {
            Firestore db = getFirestore();
            DocumentSnapshot document = db.collection(USERS_COLLECTION)
                    .whereEqualTo("email", email)
                    .get()
                    .get()
                    .getDocuments()
                    .stream().findFirst().orElse(null);

            if(document != null && document.exists()) {
                Users user = new Users();
                user.setId(document.getId());
                user.setEmail(document.getString("email"));
                user.setVerified(Boolean.TRUE.equals(document.getBoolean("verified")));

                Object createdAtValue = document.get("createdAt");
                if(createdAtValue instanceof Instant) {
                    user.setCreatedAt(Instant.parse(String.valueOf(createdAtValue)));
                }
                return user;
            }
            return null;
        } catch (Exception e) {
            log.error("Error getting user by email {}",e.getMessage());
            return null;
        }
    }

    public String saveUser(Users user) {
        try {
            Firestore db = getFirestore();
            String userId = UUID.randomUUID().toString();

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", userId);
            userData.put("email", user.getEmail());
            userData.put("verified", true);
            userData.put("createdAt", Instant.now().toString());
            userData.put("updatedAt", Instant.now().toString());

            db.collection(USERS_COLLECTION).document(userId).set(userData).get();
            return userId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user " + e);
        }
    }

    public String saveCard(Card card) {
        try {
            Firestore db = getFirestore();
            String cardId = UUID.randomUUID().toString();

            Map<String, Object> cardData = new HashMap<>();
            cardData.put("id", cardId);
            cardData.put("name", card.getName());
            cardData.put("description", card.getDescription());
            cardData.put("userId", card.getUserId());
            cardData.put("createdAt", Instant.now().toString());
            cardData.put("updatedAt", Instant.now().toString());

            db.collection(CARD_COLLECTION).document(cardId).set(cardData).get();
            return cardId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save card", e);
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
            verificationData.put("createdAt", Instant.now().toString());

            db.collection(VERIFICATION_COLLECTION).document(verificationId).set(verificationData).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save verification", e);
        }
    }

    public Verification getVerificationByEmailAndToken(String email, String token, VerificationType verificationType) {
        try {
            Firestore db = getFirestore();

            var verificationQuery = db.collection(VERIFICATION_COLLECTION)
                    .whereEqualTo("email", email)
                    .whereEqualTo("verificationType", verificationType)
                    .get()
                    .get();

            if(verificationQuery.isEmpty()) {
                return null;
            }

            for(QueryDocumentSnapshot doc: verificationQuery.getDocuments()) {
                String storedToken = doc.getString("token");

                if(storedToken == null) {
                    continue;
                }

                if(storedToken.equals(token)) {
                    String expiresAtStr = doc.getString("expiresAt");
                    if(expiresAtStr == null) {
                        return null;
                    }

                    return Verification.builder()
                            .id(doc.getString("id"))
                            .email(doc.getString("email"))
                            .verificationType(VerificationType.valueOf(doc.getString("verificationType")))
                            .token(storedToken)
                            .expiresAt(Instant.parse(expiresAtStr))
                            .createdAt(Instant.parse(Objects.requireNonNull(doc.getString("createdAt"))))
                            .build();
                } else {
                    log.warn("Token mismatch. Provided token={}, Store={}", token, storedToken);
                }
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteVerificationByEmail(String email, VerificationType verificationType) {
        try {
            Firestore db = getFirestore();
            ApiFuture<QuerySnapshot> query = db.collection(VERIFICATION_COLLECTION)
                    .whereEqualTo("email", email)
                    .whereEqualTo("verificationType", verificationType.name())
                    .get();
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
            for(QueryDocumentSnapshot doc: documents) {
                doc.getReference().delete();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete verification for email", e);
        }
    }
}
