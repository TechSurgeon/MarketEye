package com.sa.marketslayer.utils;

import com.sa.marketslayer.library.databeans.Security;
import com.sa.marketslayer.library.databeans.marketdays.a_md;
import com.sa.marketslayer.library.databeans.marketdays.b_md;
import com.sa.marketslayer.library.databeans.marketdays.c_md;
import com.sa.marketslayer.library.databeans.marketdays.d_md;
import com.sa.marketslayer.library.databeans.marketdays.e_md;
import com.sa.marketslayer.library.databeans.marketdays.f_md;
import com.sa.marketslayer.library.databeans.marketdays.g_md;
import com.sa.marketslayer.library.databeans.marketdays.h_md;
import com.sa.marketslayer.library.databeans.marketdays.i_md;
import com.sa.marketslayer.library.databeans.marketdays.k_md;
import com.sa.marketslayer.library.databeans.marketdays.l_md;
import com.sa.marketslayer.library.databeans.marketdays.m_md;
import com.sa.marketslayer.library.databeans.marketdays.n_md;
import com.sa.marketslayer.library.databeans.marketdays.o_md;
import com.sa.marketslayer.library.databeans.marketdays.p_md;
import com.sa.marketslayer.library.databeans.marketdays.q_md;
import com.sa.marketslayer.library.databeans.marketdays.r_md;
import com.sa.marketslayer.library.databeans.marketdays.s_md;
import com.sa.marketslayer.library.databeans.marketdays.t_md;
import com.sa.marketslayer.library.databeans.marketdays.u_md;
import com.sa.marketslayer.library.databeans.marketdays.v_md;
import com.sa.marketslayer.library.databeans.marketdays.w_md;
import com.sa.marketslayer.library.databeans.marketdays.x_md;
import com.sa.marketslayer.library.databeans.marketdays.y_md;
import com.sa.marketslayer.library.databeans.marketdays.z_md;

public class EntityResolver {

	public static Class resolveMarketDay(String name){
		//Class c = o.getClass();
		//System.out.println("CLASS FOUND "+c.getName());
		
		//Security s = (Security)o;
		//String name = s.name;
        if(name.startsWith("A")){
        	return a_md.class;
        }
        else if(name.startsWith("B")){
        	return b_md.class;
        }
        else if(name.startsWith("C")){
        	return c_md.class;
        }
        else if(name.startsWith("D")){
        	return d_md.class;
        }
        else if(name.startsWith("E")){
        	return e_md.class;
        }
        else if(name.startsWith("F")){
        	return f_md.class;
        }
        else if(name.startsWith("G")){
        	return g_md.class;
        }
        else if(name.startsWith("H")){
        	return h_md.class;
        }
        else if(name.startsWith("I")){
        	return i_md.class;
        }
        else if(name.startsWith("K")){
        	return k_md.class;
        }
        else if(name.startsWith("L")){
        	return l_md.class;
        }
        else if(name.startsWith("M")){
        	return m_md.class;
        }
        else if(name.startsWith("N")){
        	return n_md.class;
        }
        else if(name.startsWith("O")){
        	return o_md.class;
        }
        else if(name.startsWith("P")){
        	return p_md.class;
        }
        else if(name.startsWith("Q")){
        	return q_md.class;
        }
        else if(name.startsWith("R")){
        	return r_md.class;
        }
        else if(name.startsWith("S")){
        	return s_md.class;
        }
        else if(name.startsWith("T")){
        	return t_md.class;
        }
        else if(name.startsWith("U")){
        	return u_md.class;
        }
        else if(name.startsWith("V")){
        	return v_md.class;
        }
        else if(name.startsWith("W")){
        	return w_md.class;
        }
        else if(name.startsWith("X")){
        	return x_md.class;
        }
        else if(name.startsWith("Y")){
        	return y_md.class;
        }
        else if(name.startsWith("Z")){
        	return z_md.class;
        }
        
        return null;
		//return c;
	}
}
