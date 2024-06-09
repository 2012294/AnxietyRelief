package com.hareem.anxietyrelief;


import android.content.Context;

import java.util.Random;

public class AffirmationManager {

    private static final String[] affirmations = {
            "You are capable of achieving great things!",
            "Believe in yourself, you are unstoppable.",
            "Every day is a new opportunity to grow and improve.",
            "You have the power to overcome any challenge.",
            "Your potential is limitless.",
            "You are worthy of love and happiness."
            // Add more affirmations as needed
    };

    public static String getNewAffirmation(Context context) {
        Random random = new Random();
        int index = random.nextInt(affirmations.length);
        return affirmations[index];
    }
}
