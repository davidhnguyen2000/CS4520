package com.example.inclassassignments;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Profile implements Serializable {
    public String name;
    public String email;
    public int avatarId;
    public String operatingSystem;
    public int mood;

    public Profile(String name, String email, int avatarId, int operatingSystem, int mood) {
        this.email = email;
        this.name = name;
        if (avatarId < 1 || avatarId > 6) {
            throw new IllegalArgumentException("Invalid avatarId");
        } else {
            this.avatarId = avatarId;
        }
        if (operatingSystem != R.id.radioButtonIOS && operatingSystem != R.id.radioButtonAndroid) {
            throw new IllegalArgumentException("Invalid operatingSystem");
        } else if (operatingSystem == R.id.radioButtonAndroid) {
            this.operatingSystem = "Android";
        } else {
            this.operatingSystem = "iOS";
        }
        if (mood < 0 || mood > 3) {
            throw new IllegalArgumentException("Invalid mood");
        } else {
            this.mood = mood;
        }
    }

    public Profile () {
    }

    public String getMood() {
        switch (mood) {
            case 0: return "Angry";
            case 1: return "Sad";
            case 2: return "Happy";
            case 3: return "Awesome";
        }
        return "";
    }

    @NonNull
    @Override
    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", avatarId=" + avatarId +
                ", operatingSystem='" + operatingSystem + '\'' +
                ", mood=" + mood +
                '}';
    }
}
