package com.dongao.textimage;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * @author jjr
 * @date 2018/6/19
 */
public class MainAdapter extends BaseMultiItemQuickAdapter<Item, BaseViewHolder> {

    public static final int TYPE_0 = 0;
    public static final int TYPE_1 = 1;
    public static final int TYPE_2 = 2;
    public static final int TYPE_3 = 3;

    private Activity mActivity;

    public MainAdapter(Activity activity, @Nullable List<Item> data) {
        super(data);
        mActivity = activity;
        addItemTypes();
    }

    private void addItemTypes() {
        addItemType(TYPE_0, R.layout.item_main_0);
        addItemType(TYPE_1, R.layout.item_main_1);
        addItemType(TYPE_2, R.layout.item_main_2);
        addItemType(TYPE_3, R.layout.item_main_3);
    }

    @Override
    protected void convert(BaseViewHolder helper, Item item) {
        switch (item.getItemType()) {
            case TYPE_0:
                convertType0(helper, item);
                break;
            case TYPE_1:
                convertType1(helper, item);
                break;
            case TYPE_2:
                convertType2(helper, item);
                break;
            case TYPE_3:
                convertType3(helper, item);
                break;
            default:
                break;
        }
    }

    private void convertType3(BaseViewHolder helper, Item item) {
        ((HtmlTextView) helper.itemView).setHtml(item.getContent(), new HtmlHttpImageGetter(((HtmlTextView) helper.itemView)));
    }

    private void convertType2(BaseViewHolder helper, Item item) {
        Spanned result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            result = Html.fromHtml(item.getContent(), Html.FROM_HTML_MODE_LEGACY, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    InputStream is = null;
                    try {
                        is = (InputStream) new URL(source).getContent();
                        Drawable d = Drawable.createFromStream(is, "src");
                        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                        is.close();
                        return d;
                    } catch (Exception e) {
                        return null;
                    }
                }
            }, null);
        } else {
            result = Html.fromHtml(item.getContent(), new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    InputStream is = null;
                    try {
                        is = (InputStream) new URL(source).getContent();
                        Drawable d = Drawable.createFromStream(is, "src");
                        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                        is.close();
                        return d;
                    } catch (Exception e) {
                        return null;
                    }
                }
            }, null);
        }
        ((TextView) helper.itemView).setText(result);
    }

    private void convertType1(BaseViewHolder helper, Item item) {
        ((MixtureTextView) helper.itemView).setText(item.getTitle());
        if (item.getImgs().size() == 1) {
            Glide.with(mActivity).load(item.getImgs().get(0)).into((ImageView) helper.getView(R.id.img1));
        } else if (item.getImgs().size() == 3) {
            Glide.with(mActivity).load(item.getImgs().get(0)).into((ImageView) helper.getView(R.id.img1));
            Glide.with(mActivity).load(item.getImgs().get(1)).into((ImageView) helper.getView(R.id.img2));
            Glide.with(mActivity).load(item.getImgs().get(2)).into((ImageView) helper.getView(R.id.img3));
        }
    }

    private void convertType0(BaseViewHolder helper, Item item) {
        helper.setText(R.id.title_tv, item.getTitle());
        if (item.getImgs().size() == 1) {
            helper.setGone(R.id.imgs_fl, false)
                    .setGone(R.id.img, true);
            Glide.with(mActivity).load(item.getImgs().get(0)).into((ImageView) helper.getView(R.id.img));
        } else if (item.getImgs().size() == 3) {
            helper.setGone(R.id.imgs_fl, true)
                    .setGone(R.id.img, false);
            FlowLayout flowLayout = helper.<FlowLayout>getView(R.id.imgs_fl);
            for (int i = 0; i < flowLayout.getChildCount(); i++) {
                Glide.with(mActivity).load(item.getImgs().get(i)).into((ImageView) flowLayout.getChildAt(i));
            }
        }
    }
}
