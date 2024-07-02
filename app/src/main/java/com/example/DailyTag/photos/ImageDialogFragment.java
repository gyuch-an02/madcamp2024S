package com.example.DailyTag.photos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.DailyTag.R;
import com.example.DailyTag.utils.TagUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ImageDialogFragment extends DialogFragment {

    private static final String ARG_IMAGE_PATH = "image_path";
    private static final String ARG_TAGS = "image_tags";

    public static ImageDialogFragment newInstance(String imagePath, Set<String> tags) {
        ImageDialogFragment fragment = new ImageDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_PATH, imagePath);
        args.putStringArrayList(ARG_TAGS, new ArrayList<>(tags));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_dialog, container, false);

        ImageView imageView = view.findViewById(R.id.enlargedImageView);
        TextView imageNameTextView = view.findViewById(R.id.imageNameTextView);
        TextView imageDateTextView = view.findViewById(R.id.imageDateTextView);
        LinearLayout tagContainer = view.findViewById(R.id.tagContainer);

        if (getArguments() != null) {
            String imagePath = getArguments().getString(ARG_IMAGE_PATH);
            assert imagePath != null;
            File imageFile = new File(imagePath);

            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);

            String imageName = imageFile.getName();
            String imageDate = DateFormat.format("yyyy-MM-dd hh:mm:ss", new Date(imageFile.lastModified())).toString();

            imageNameTextView.setText(imageName);
            imageDateTextView.setText(imageDate);

            ArrayList<String> tags = getArguments().getStringArrayList(ARG_TAGS);
            if (tags != null && !tags.isEmpty()) {
                TagUtils.renewTagLayout(getContext(), tagContainer, new HashSet<>(tags), null);
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
    }
}