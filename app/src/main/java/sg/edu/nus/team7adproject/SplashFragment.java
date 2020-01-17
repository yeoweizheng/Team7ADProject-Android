package sg.edu.nus.team7adproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SplashFragment extends Fragment {
    private ISplashFragment iSplashFragment;
    private NavController navController;

    public SplashFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        //navController.navigate(R.id.loginFragment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        iSplashFragment  = (ISplashFragment) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        iSplashFragment = null;
    }

    public interface ISplashFragment {
    }
}
