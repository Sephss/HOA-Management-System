package com.example.hoamanagementsystem.FirebaseServices;

import com.example.hoamanagementsystem.FirebaseServices.callback.CreateFinanceRequestCallback;
import com.example.hoamanagementsystem.Model.FinancialModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseFinancialManager {
    private static DatabaseReference getDatabase() {
        return FirebaseDatabase.getInstance().getReference("Finance");
    }
    private void createFinanceRequest(FinancialModel data, CreateFinanceRequestCallback callback) {
        String requestID = getDatabase().push().getKey();
        if(requestID == null) {
            callback.onFailure("Failed to generate requestID");
            return;
        }


        data.setRequestID(requestID);

    }
}
