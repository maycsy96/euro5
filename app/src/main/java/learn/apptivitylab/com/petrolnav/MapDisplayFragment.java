package learn.apptivitylab.com.petrolnav;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by apptivitylab on 09/01/2018.
 */

public class MapDisplayFragment extends Fragment{
    public static final String TAG ="MapDisplayFragment";
    public static final String DISPLAY_MAP ="displaymap";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_display,container,false);
        return rootView;
    }


}
