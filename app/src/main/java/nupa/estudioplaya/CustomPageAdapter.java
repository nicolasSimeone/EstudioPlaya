package nupa.estudioplaya;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by xelere-lenovo on 16/11/2016.
 */

public class CustomPageAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;

    int[] mTitulo = {
            R.string.titulo0,
            R.string.titulo1,
            R.string.titulo2,
            R.string.titulo3,
            R.string.titulo4,
            R.string.titulo5,
            R.string.titulo6,
            R.string.titulo7,
            R.string.titulo8,
            R.string.titulo9,
            R.string.titulo10
    };

    int[] mImagen = {
            R.drawable.programa_0,
            R.drawable.programa_1,
            R.drawable.programa_2,
            R.drawable.programa_3,
            R.drawable.programa_4,
            R.drawable.programa_5,
            R.drawable.programa_6,
            R.drawable.programa_7,
            R.drawable.programa_8,
            R.drawable.programa_9,
            R.drawable.programa_10

    };

    int[] mDescripcion = {
            R.string.descripcion0,
            R.string.descripcion1,
            R.string.descripcion2,
            R.string.descripcion3,
            R.string.descripcion4,
            R.string.descripcion5,
            R.string.descripcion6,
            R.string.descripcion7,
            R.string.descripcion8,
            R.string.descripcion9,
            R.string.descripcion10
    };

    public CustomPageAdapter(Context context){
        mContext=context;
        mLayoutInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return mTitulo.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);



        TextView txtTitulo = (TextView) itemView.findViewById(R.id.txtTitulo);
        txtTitulo.setText(mTitulo[position]);

        ImageView imageViewPrograma = (ImageView) itemView.findViewById(R.id.imgImagenPrograma);
        imageViewPrograma.setImageResource(mImagen[position]);

        TextView txtDescripcion = (TextView) itemView.findViewById(R.id.txtDescripcion);
        txtDescripcion.setText(mDescripcion[position]);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout) object);
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==((ConstraintLayout) object);
    }


}
