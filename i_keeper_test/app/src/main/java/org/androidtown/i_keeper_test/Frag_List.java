package org.androidtown.i_keeper_test;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_List extends Fragment {
    //listView를 사용하기 위한 필요 변수들
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> list;

    public Frag_List() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.frag_list, container, false);

        //초기화
        list = new ArrayList<String>();
        adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,list);
        listView=(ListView)rootView.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        list.add("for test");
        adapter.notifyDataSetChanged();

        //listView에서 item을 클릭했을 때 하는 이벤트를 정의한다.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        return rootView;
    }

}
