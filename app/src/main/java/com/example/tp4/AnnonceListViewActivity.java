package com.example.tp4;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AnnonceListViewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Annonce> mockList = makeMockList();

        mAdapter = new AnnonceListAdapter(mockList);
        recyclerView.setAdapter(mAdapter);
    }

    private static List<Annonce> makeMockList() {
        List<Annonce> mockList = new ArrayList<>();
        mockList.add(new Annonce("5a6a48033a66d996fc999c71",
                "Lorem aute elit aliqua veniam",
                "Exercitation elit nostrud dolore pariatur id enim enim excepteur elit pariatur. Cupidatat consectetur proident qui nisi quis aute sit incididunt nulla tempor aute ipsum sit. Sunt duis duis elit laboris dolore ex in voluptate id duis deserunt in consequat.",
                52,
                "Esperanza",
                "esperanzaclay@datagen.com",
                "38.98.64.70.49",
                "qui ipsum ea labore sunt",
                "95206",
                makeStringList("http://farm5.staticflickr.com/4609/38984233005_99ebb2a81a_q.jpg", "http://farm5.staticflickr.com/4760/39887610041_6f6820f60a_q.jpg", "http://farm5.staticflickr.com/4669/28115625339_d960cf228a_q.jpg"),
                1516502151
        ));

        mockList.add(new Annonce("5a6a4803e2c3b7b6cd7e5eba",
                "pariatur officia cupidatat sunt aute",
                "Incididunt est exercitation exercitation amet cupidatat qui labore eiusmod magna aute. Consectetur exercitation excepteur do qui commodo et velit laborum id adipisicing qui. Mollit commodo sint sint consectetur duis ipsum ipsum labore.",
                134,
                "Adriana",
                "adrianaclay@datagen.com",
                "64.39.81.97.73",
                "ad et qui sit eiusmod",
                "76942",
                makeStringList("http://farm5.staticflickr.com/4750/38928320495_aac4b0bc70_q.jpg", "http://farm5.staticflickr.com/4744/38960600945_fd78a28b4f_q.jpg", "http://farm5.staticflickr.com/4617/28117382549_64ee8dff4c_q.jpg"),
                1516902354
        ));

        mockList.add(new Annonce("5a6a4803a70b36aa0d0b4c7f",
                "dolore ad anim commodo aliqua",
                "Ad tempor elit eiusmod qui exercitation ea cillum Lorem irure tempor reprehenderit. Minim deserunt pariatur aute nostrud fugiat proident voluptate ea amet. Anim ipsum ea nostrud anim nisi excepteur ex amet non commodo aute fugiat.",
                275,
                "Cooley",
                "cooleyclay@datagen.com",
                "41.57.69.54.53",
                "pariatur excepteur mollit pariatur elit",
                "18828",
                makeStringList("http://farm5.staticflickr.com/4704/39825436801_d3242a903c_q.jpg", "http://farm5.staticflickr.com/4671/25002092047_3262fc12a4_q.jpg", "http://farm5.staticflickr.com/4657/38970125465_2f880c00dd_q.jpg"),
                1516807212
        ));

        mockList.add(new Annonce("5a6a4803dfb5ec01fc6e7d21",
                "tempor ullamco culpa anim nulla",
                "Duis sunt minim reprehenderit duis cillum culpa. Ullamco aliqua commodo nulla irure nisi cupidatat. Tempor qui cupidatat minim pariatur sunt reprehenderit culpa enim.",
                56,
                "Robin",
                "robinclay@datagen.com",
                "77.67.73.24.57",
                "velit dolor quis in aute",
                "13142",
                makeStringList("http://farm5.staticflickr.com/4654/39778867762_3737534605_q.jpg", "http://farm5.staticflickr.com/4763/39132150144_a8791871e0_q.jpg", "http://farm5.staticflickr.com/4671/39813441251_6ba5153b27_q.jpg"),
                1516519838
        ));

        //5e mockAnnonce
        mockList.add(new Annonce("5a6a4803562613a36c318508",
                "fugiat incididunt sunt nulla dolor",
                "Labore commodo do consequat do voluptate pariatur veniam aliquip. In officia ex excepteur velit commodo do in. Ea sit fugiat pariatur ullamco ex velit sit.",
                119,
                "Mcfarland",
                "mcfarlandclay@datagen.com",
                "93.77.34.27.69",
                "ipsum reprehenderit anim ut veniam",
                "63810",
                makeStringList("http://farm5.staticflickr.com/4712/39837547902_04f15943e7_q.jpg", "http://farm5.staticflickr.com/4676/39863866052_563c4eca6d_q.jpg", "http://farm5.staticflickr.com/4614/24981367097_c919fca391_q.jpg", "http://farm5.staticflickr.com/4718/28088913019_1ac6f44739_q.jpg"),
                1516158357
        ));

        return mockList;
    }

    private static List<String> makeStringList(String... uris) {
        List<String> stringList = new ArrayList();
        for (String uri : uris) {
            stringList.add(uri);
        }
        return stringList;
    }
}
