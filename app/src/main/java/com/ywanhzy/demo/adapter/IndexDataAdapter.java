package com.ywanhzy.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ywanhzy.demo.R;
import com.ywanhzy.demo.entity.MenuEntity;

import java.util.List;

import cn.bingoogolapple.badgeview.BGABadgeImageView;

public class IndexDataAdapter extends BaseAdapter {

    public List<MenuEntity> list;

    private LayoutInflater layoutInflater;
    private Context context;

    public IndexDataAdapter(Context context, List<MenuEntity> list) {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MenuEntity entity = null;
        View view = null;
        entity = list.get(position);
        if (layoutInflater != null) {
            view = layoutInflater.inflate(R.layout.item_gridview, null);

            BGABadgeImageView iv_gridview = (BGABadgeImageView) view.findViewById(R.id.iv_gridview);
            TextView tv_gridview = (TextView) view.findViewById(R.id.tv_gridview);

            tv_gridview.setText(entity.getTitle());
            if (entity.getNum().equals("0")) {
                iv_gridview.hiddenBadge();
            } else {
                iv_gridview.showTextBadge(entity.getNum());
            }
            //获取资源图片
            int drawableId = context.getResources().getIdentifier(entity.getIco(),"mipmap", context.getPackageName());
            iv_gridview.setImageResource(drawableId);

            /** 网络图片
            if (!entity.getIco().isEmpty()) {
                Glide.with(context).load(entity.getIco()).into(iv_gridview);
            } else {
                Glide.with(context).load(R.mipmap.all_big_ico)
                        .placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
                        .into(iv_gridview);
            }
             */

            //设置边框及颜色
//			iv_gridview.getBadgeViewHelper().setBadgeBorderWidthDp(2);
//			iv_gridview.getBadgeViewHelper().setBadgeBorderColorInt(Color.parseColor("#0000FF"));
        }
        return view;
    }
}
