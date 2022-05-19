package com.example.inclassassignments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InClass03SelectAvatar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InClass03SelectAvatar extends Fragment {

    public IFromSelectAvatarToActivity sendData;

    public InClass03SelectAvatar() {
        // Required empty public constructor
    }

    public static InClass03SelectAvatar newInstance() {
        return new InClass03SelectAvatar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_in_class03_select_avatar_activity, container, false);
        ImageView imageViewAvatar1 = rootView.findViewById(R.id.imageViewFragmentAvatar_f_1);
        ImageView imageViewAvatar2 = rootView.findViewById(R.id.imageViewFragmentAvatar_f_2);
        ImageView imageViewAvatar3 = rootView.findViewById(R.id.imageViewFragmentAvatar_f_3);
        ImageView imageViewAvatar4 = rootView.findViewById(R.id.imageViewFragmentAvatar_m_1);
        ImageView imageViewAvatar5 = rootView.findViewById(R.id.imageViewFragmentAvatar_m_2);
        ImageView imageViewAvatar6 = rootView.findViewById(R.id.imageViewFragmentAvatar_m_3);

        imageViewAvatar1.setOnClickListener(v -> sendData.fromSelectAvatarFragment(1));
        imageViewAvatar2.setOnClickListener(v -> sendData.fromSelectAvatarFragment(2));
        imageViewAvatar3.setOnClickListener(v -> sendData.fromSelectAvatarFragment(3));
        imageViewAvatar4.setOnClickListener(v -> sendData.fromSelectAvatarFragment(4));
        imageViewAvatar5.setOnClickListener(v -> sendData.fromSelectAvatarFragment(5));
        imageViewAvatar6.setOnClickListener(v -> sendData.fromSelectAvatarFragment(6));

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IFromSelectAvatarToActivity) {
            sendData = (IFromSelectAvatarToActivity) context;
        } else {
            throw new RuntimeException(context + " must implement IFromSelectAvatarToActivity");
        }
    }

    public interface IFromSelectAvatarToActivity {
        void fromSelectAvatarFragment(int avatarNumber);
    }
}