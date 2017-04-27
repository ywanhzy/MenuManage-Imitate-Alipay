package com.ywanhzy.demo.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ywanhzy.demo.AppConfig;
import com.ywanhzy.demo.AppContext;
import com.ywanhzy.demo.R;
import com.ywanhzy.demo.drag.DragAdapterInterface;
import com.ywanhzy.demo.entity.MenuEntity;
import com.ywanhzy.demo.ui.MenuManageActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MyAdapter extends BaseAdapter implements DragAdapterInterface {
	private boolean IsEdit = false;
	private List<MenuEntity> datas = new ArrayList<MenuEntity>();
	private MenuManageActivity context;
	private AppContext appContext;

	public MyAdapter(MenuManageActivity context, AppContext appContext, List<MenuEntity> datas) {
		this.context = context;
		this.appContext = appContext;
		this.datas = datas;
	}

	public void setDatas(List<MenuEntity> datas) {
		this.datas.clear();
		this.datas.addAll(datas);
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		MenuEntity bean = datas.get(position);
		Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(context).inflate(R.layout.view_item, null);
			holder.deleteImg = (ImageView) convertView.findViewById(R.id.delete_img);
			holder.iconImg = (ImageView) convertView.findViewById(R.id.icon_img);
			holder.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
			holder.container = convertView.findViewById(R.id.item_container);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.deleteImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				context.DelMeun(datas.get(position),position);
				datas.remove(position);
				String key = AppConfig.KEY_USER_TEMP;
				appContext.saveObject((Serializable) datas, key);
			}
		});
		if (IsEdit) {
			holder.deleteImg.setVisibility(View.VISIBLE);
		} else {
			holder.deleteImg.setVisibility(View.GONE);
		}
        //获取资源图片
        int drawableId = context.getResources().getIdentifier(bean.getIco(),"mipmap", context.getPackageName());
        holder.iconImg.setImageResource(drawableId);

//		Glide.with(context).load(bean.getIco()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
//				.into(holder.iconImg);

		holder.nameTv.setText(bean.getTitle());
		holder.container.setBackgroundColor(Color.WHITE);
		return convertView;
	}

	class Holder {
		public ImageView deleteImg;
		public ImageView iconImg;
		public TextView nameTv;
		public View container;
	}

	@Override
	public void reOrder(int startPosition, int endPosition) {
		if (endPosition < datas.size()) {
			MenuEntity object = datas.remove(startPosition);
			datas.add(endPosition, object);
			String key = AppConfig.KEY_USER_TEMP;
			appContext.saveObject((Serializable) datas, key);
			notifyDataSetChanged();
		}
	}

	public void setEdit() {
		IsEdit = true;
		notifyDataSetChanged();
	}

	public void getDatas() {
		for (MenuEntity data : datas) {
			// DebugLog.i("点击 Item " + data.getId());
		}
	}

	public void endEdit() {
		IsEdit = false;
		notifyDataSetChanged();
	}
	public boolean getEditStatue() {
		return IsEdit;
	}


}
