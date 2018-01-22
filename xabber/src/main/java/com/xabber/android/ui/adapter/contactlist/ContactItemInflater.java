package com.xabber.android.ui.adapter.contactlist;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.xabber.android.R;
import com.xabber.android.data.SettingsManager;
import com.xabber.android.data.extension.muc.MUCManager;
import com.xabber.android.ui.activity.ContactActivity;
import com.xabber.android.ui.activity.ContactEditActivity;
import com.xabber.android.ui.adapter.contactlist.viewobjects.ContactVO;
import com.xabber.android.ui.color.ColorManager;

class ContactItemInflater {

    private final Context context;
    private String outgoingMessageIndicatorText;

    ContactItemInflater(Context context) {
        this.context = context;
        outgoingMessageIndicatorText = context.getString(R.string.sender_is_you) + ": ";
    }

    void bindViewHolder(ContactListItemViewHolder viewHolder, final ContactVO viewObject) {

        if (viewObject.isShowOfflineShadow())
            viewHolder.offlineShadow.setVisibility(View.VISIBLE);
        else viewHolder.offlineShadow.setVisibility(View.GONE);

        viewHolder.accountColorIndicator.setBackgroundColor(viewObject.getAccountColorIndicator());

        if (SettingsManager.contactsShowAvatars()) {
            viewHolder.ivAvatar.setVisibility(View.VISIBLE);
            viewHolder.ivStatus.setVisibility(View.VISIBLE);
            viewHolder.ivAvatar.setImageDrawable(viewObject.getAvatar());
            viewHolder.ivOnlyStatus.setVisibility(View.GONE);
        } else {
            viewHolder.ivAvatar.setVisibility(View.GONE);
            viewHolder.ivStatus.setVisibility(View.GONE);
            viewHolder.ivOnlyStatus.setVisibility(View.VISIBLE);
        }

        viewHolder.tvContactName.setText(viewObject.getName());

        Drawable mucIndicator;
        if (viewObject.getMucIndicatorLevel() == 0)
            mucIndicator = null;
        else {
            mucIndicator = context.getResources().getDrawable(R.drawable.muc_indicator_view);
            mucIndicator.setLevel(viewObject.getMucIndicatorLevel());
        }

        if (viewObject.getStatusLevel() == 6) {
            viewHolder.ivStatus.setVisibility(View.INVISIBLE);
            //viewHolder.ivDevice.setVisibility(View.INVISIBLE);
            viewHolder.tvStatus.setTextColor(ColorManager.getInstance().getColorContactSecondLine());
        } else {
            viewHolder.ivStatus.setVisibility(View.VISIBLE);
            //viewHolder.ivDevice.setVisibility(View.VISIBLE);
            viewHolder.tvStatus.setTextColor(context.getResources().getColor(R.color.status_color_in_contact_list_online));
        }
        viewHolder.ivStatus.setImageLevel(viewObject.getStatusLevel());
        viewHolder.ivOnlyStatus.setImageLevel(viewObject.getStatusLevel());

        String statusText = viewObject.getStatus();
        if (statusText.isEmpty()) statusText = context.getString(viewObject.getStatusId());

        viewHolder.tvStatus.setText(statusText);

        if (viewObject.getUnreadCount() > 0) {
            viewHolder.tvUnreadCount.setText(String.valueOf(viewObject.getUnreadCount()));
            viewHolder.tvUnreadCount.setVisibility(View.VISIBLE);
        } else viewHolder.tvUnreadCount.setVisibility(View.GONE);

        // notification mute
        Resources resources = context.getResources();
        switch (viewObject.getNotificationMode()) {
            case enabled:
                viewHolder.tvContactName.setCompoundDrawablesWithIntrinsicBounds(mucIndicator, null,
                        resources.getDrawable(R.drawable.ic_unmute), null);
                break;
            case disabled:
                viewHolder.tvContactName.setCompoundDrawablesWithIntrinsicBounds(mucIndicator, null,
                        resources.getDrawable(R.drawable.ic_mute), null);
                break;
            default:
                viewHolder.tvContactName.setCompoundDrawablesWithIntrinsicBounds(
                        mucIndicator, null, null, null);
        }

        if (viewObject.isMute())
            viewHolder.tvUnreadCount.getBackground().mutate().setColorFilter(
                    resources.getColor(R.color.grey_500),
                    PorterDuff.Mode.SRC_IN);
        else viewHolder.tvUnreadCount.getBackground().mutate().clearColorFilter();
    }

    void onAvatarClick(ContactVO contact) {
        Intent intent;
        if (MUCManager.getInstance().hasRoom(contact.getAccountJid(), contact.getUserJid())) {
            intent = ContactActivity.createIntent(context, contact.getAccountJid(), contact.getUserJid());
        } else {
            intent = ContactEditActivity.createIntent(context, contact.getAccountJid(), contact.getUserJid());
        }
        context.startActivity(intent);
    }
}
