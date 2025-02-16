package com.example.DailyTag.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;

import com.example.DailyTag.R;
import com.example.DailyTag.contacts.ContactDetailsActivity;
import com.example.DailyTag.contacts.ContactManager;

import java.util.Set;

public class TagUtils {

    public static void renewTagLayout(Context context, LifecycleOwner lifecycleOwner, TagViewModel tagViewModel, LinearLayout tagContainer, String identifier, View.OnClickListener onClickListener) {
        tagContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(context);
        int tagCount = 0;
        LinearLayout currentLine = createNewLine(context, tagContainer);
        Set<Tag> tagList = tagViewModel.loadTags(identifier).getValue();
        if (tagList != null) {
            for (Tag tag : tagList) {
                if (tagCount == 3) { // Limit tags per line
                    currentLine = createNewLine(context, tagContainer);
                    tagCount = 0;
                }
                View tagView = inflater.inflate(R.layout.item_tag, currentLine, false);
                TextView tagTextView = tagView.findViewById(R.id.tagTextView);
                tagTextView.setText(tag.getContactName());
                tagView.setTag(tag); // Store the tag object in the view
                currentLine.addView(tagView);

                tagView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ContactDetailsActivity.class);
                    intent.putExtra("CONTACT_ID", tag.getContactId());
                    context.startActivity(intent);
                });

                tagView.findViewById(R.id.deleteButton).setOnClickListener(v -> {
                    tagViewModel.removeTag(identifier, tag);
                    onClickListener.onClick(tagView);
                });
                tagCount++;
            }
        }
        addAddTagButton(context, tagContainer, tagViewModel, identifier, onClickListener, lifecycleOwner);
    }

    private static LinearLayout createNewLine(Context context, LinearLayout tagContainer) {
        LinearLayout newLine = new LinearLayout(context);
        newLine.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        newLine.setOrientation(LinearLayout.HORIZONTAL);
        newLine.setPadding(8, 8, 8, 8);
        tagContainer.addView(newLine);
        return newLine;
    }

    public static void addAddTagButton(Context context, LinearLayout tagContainer, TagViewModel tagViewModel, String identifier, View.OnClickListener onClickListener, LifecycleOwner lifecycleOwner) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View addTagView = inflater.inflate(R.layout.item_add_tag, tagContainer, false);
        AutoCompleteTextView addTagTextView = addTagView.findViewById(R.id.addTagTextView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, ContactManager.getContactNames(context));
        addTagTextView.setAdapter(adapter);

        addTagTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTag = adapter.getItem(position);
            if (selectedTag != null && !selectedTag.isEmpty()) {
                long contactId = ContactManager.getContactIdByName(context, selectedTag);
                Tag tag = new Tag(contactId, selectedTag, identifier);
                Set<Tag> currentTags = tagViewModel.loadTags(identifier).getValue();
                if (currentTags != null && currentTags.contains(tag)) {
                    Toast.makeText(context, "태그가 이미 존재합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    tagViewModel.addTag(identifier, tag);
                    renewTagLayout(context, lifecycleOwner, tagViewModel, tagContainer, identifier, onClickListener);
                }
            }
        });

        tagContainer.addView(addTagView);
    }
}
