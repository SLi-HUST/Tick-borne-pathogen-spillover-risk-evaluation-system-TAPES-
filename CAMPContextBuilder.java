package camp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Iterator;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.continuous.NdPoint;
//import repast.simphony.engine.environment.RunEnvironment;
//import repast.simphony.engine.schedule.ScheduleParameters;
//import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.valueLayer.GridValueLayer;


public class CAMPContextBuilder implements ContextBuilder<Object> {
	
	//public DefaultTableModel tm_DON, tm_DIN, tm_NIP;
	//public JFrame myFrame; 
	public static Context<Object> mainContext;
	public static Grid<Object> mainGrid;
	
	@Override
    public Context<Object> build(Context<Object> context) {
    	
    	int gridWidth = 494; //(Integer)parm.getValue("gridWidth");
        int gridHeight = 347;// (Integer)parm.getValue("gridHeight");
    	
		mainContext = context;
		mainContext.setId("CAMP");
        
    	System.out.println(">>>>>> initialising : map size (X * Y) " + gridWidth + " * " + gridHeight);
    	
    	//String data_file="input/test_data.txt";
    	//String data_file="input/lyr_data.txt";
    	
		// Create the grid for the CAs
    	mainGrid = GridFactoryFinder.createGridFactory(null).createGrid("Grid", context,
                new GridBuilderParameters<Object>(new WrapAroundBorders(),
                        new RandomGridAdder<Object>(), false, gridWidth, gridHeight));

        // Create value layers to store the states for each CA
        GridValueLayer valueLayer0 = new GridValueLayer("TotoLarvaTick",true, new WrapAroundBorders(), gridWidth, gridHeight);
        GridValueLayer valueLayer1 = new GridValueLayer("TotoNymphTick",true, new WrapAroundBorders(), gridWidth, gridHeight);
        GridValueLayer valueLayer2 = new GridValueLayer("TotoAdultTick",true, new WrapAroundBorders(), gridWidth, gridHeight);
        GridValueLayer valueLayer3 = new GridValueLayer("TotoReserviorHost",true, new WrapAroundBorders(), gridWidth, gridHeight);
        GridValueLayer valueLayer4 = new GridValueLayer("TotoReproductionHost",true, new WrapAroundBorders(), gridWidth, gridHeight);
        GridValueLayer valueLayer5 = new GridValueLayer("TotoLivestockHost",true, new WrapAroundBorders(), gridWidth, gridHeight);
        GridValueLayer valueLayer6 = new GridValueLayer("NymphalInfectionPrevalence",true, new WrapAroundBorders(), gridWidth, gridHeight);
        GridValueLayer valueLayer7 = new GridValueLayer("InfectiousNymphTick",true, new WrapAroundBorders(), gridWidth, gridHeight);
        GridValueLayer valueLayer8 = new GridValueLayer("ContactRate",true, new WrapAroundBorders(), gridWidth, gridHeight);
        //GridValueLayer valueLayer6 = new GridValueLayer("InfectiousLarvaTick",true, new WrapAroundBorders(), gridWidth, gridHeight);
       //GridValueLayer valueLayer7 = new GridValueLayer("InfectiousNymphTick",true, new WrapAroundBorders(), gridWidth, gridHeight);
        //GridValueLayer valueLayer8 = new GridValueLayer("InfectiousAdultTick",true, new WrapAroundBorders(), gridWidth, gridHeight);
        //GridValueLayer valueLayer9 = new GridValueLayer("InfectiousReserviorHost",true, new WrapAroundBorders(), gridWidth, gridHeight);
       
        
        //GridValueLayer valueLayer10 = new GridValueLayer("Landscape",true, new WrapAroundBorders(), gridWidth, gridHeight);
       // GridValueLayer valueLayer11 = new GridValueLayer("Elevation",true, new WrapAroundBorders(), gridWidth, gridHeight);

        // Add the value layers to the context
        context.addValueLayer(valueLayer0);
        context.addValueLayer(valueLayer1);
        context.addValueLayer(valueLayer2);
        context.addValueLayer(valueLayer3);
        context.addValueLayer(valueLayer4);
        context.addValueLayer(valueLayer5);
        context.addValueLayer(valueLayer6);
        context.addValueLayer(valueLayer7);
        context.addValueLayer(valueLayer8);
       // context.addValueLayer(valueLayer8);
        //context.addValueLayer(valueLayer9);
    	   
        //configureMap("input/lyr_hu.csv");
        configureMap("input/lyr_hu_test.csv");
        configureSettlements();
       
		return mainContext;
		
    }
    
    private void configureSettlements(){
    	
    	System.out.println(">>> creating 3176 settlements");
    	
    	for(int i = 0; i<3176; i++){
    		
    		CAMPSettlement stt = new CAMPSettlement();	    		
    		
    		stt.s_ID = i +1;	    		
    		stt.initialiseCells();
    		
    		Iterator<Object> cels = CAMPContextBuilder.mainContext.getObjects(CAMPCA.class).iterator();
        	
        	while(cels.hasNext()) {
        		
        		CAMPCA ccc = (CAMPCA)(cels.next());	
        		if (ccc.region_ID()==stt.s_ID){
        			stt.s_cells.add(ccc);  				
        		}
        	}
        	
        	stt.calculateXY();
        	stt.initialiseSettlement();

        	//stt.HU_NaturalQuality = 0;
        	
        	if (stt.x + stt.y > 0) {
        		CAMPContextBuilder.mainContext.add(stt);
        		//CAMPContextBuilder.mainGrid.moveTo(stt, stt.x, stt.y);
        	}
        	   		
    		//CAMPContextBuilder.mainGrid.moveTo(stt, 0, 0);
    		//System.out.println(i);
    	}
    	
    	System.out.println(">>> total number of settlements configured:" + CAMPContextBuilder.mainContext.getObjects(CAMPSettlement.class).size());
    	
    	//Iterator<Object> sttls = CAMPContextBuilder.mainContext.getObjects(CAMPSettlement.class).iterator(); 

    	//System.out.println(CAMPContextBuilder.mainContext.getObjects(CAMPSettlement.class).size());
    }
	
    private void configureMap(String data_file){
		
    	try {
			   
			BufferedReader bufRdr  = new BufferedReader(new FileReader(data_file));
	        String line = null;
	        
	        int row =0;
	        
	        int i_id = 0, i_x=0, i_y =0, i_elevation = 0, i_slope = 0, i_s_ID = 0; 
	        int i_1stmonth = 0;
	        int i_pop = 0, i_p_res = 0, i_p_com = 0, i_p_gre = 0, i_d2capital= 0, i_d2town= 0, i_d2village= 0, i_d2river= 0, i_d2lake= 0;
	        int i_p_pasture = 0, i_p_blwood= 0, i_p_cfwood= 0, i_p_mxwood= 0, i_p_ngrass= 0, i_p_ecotone= 0; 
	        int n_blwood= 0, n_cfwood= 0, n_mxwood= 0, n_ngrass= 0, n_ecotone= 0, n_urban =0, n_other=0;     
	        double n_toto_nq = 0;
  	
	    	//Iterator<Object> sttls = CAMPContextBuilder.mainContext.getObjects(CAMPSettlement.class).iterator();
			
            System.out.println(">>> loading cell data");
            
            Vector<Double> idData = new Vector<Double>();
            
            while((line = bufRdr.readLine()) != null){                
            	//System.out.println(line);
            	StringTokenizer st = new StringTokenizer(line,",");
            	ArrayList line_data = new ArrayList();
            	while (st.hasMoreTokens()){
                    //get next token and store it in the <strong class="highlight">array</strong>
                	String str = st.nextToken();
                	line_data.add(str);
                    //col++;
                }
            	
            	if (row == 0){
            		
            		System.out.println(">>> reading header ");
            		
            		for (int i = 0; i<line_data.size(); i ++){
            			String sss = (String) line_data.get(i);
            			if (sss.equalsIgnoreCase("Cell_ID")) {i_id = i; System.out.print("/ Cell_ID at col:" + i);};
            			if (sss.equalsIgnoreCase("m_x")) {i_x = i; System.out.print("/ x at col:" + i);};
            			if (sss.equalsIgnoreCase("m_y")) {i_y = i; System.out.print("/ y at col:" + i);};
            			if (sss.equalsIgnoreCase("pop")) {i_pop = i; System.out.print("/ pop at col:" + i);};
            			if (sss.equalsIgnoreCase("elev")) {i_elevation = i; System.out.print("/ elev at col:" + i);};
            			if (sss.equalsIgnoreCase("slope")) {i_slope = i; System.out.print("/ slope at col:" + i);};
            			if (sss.equalsIgnoreCase("MEAN_T_m01")) {i_1stmonth = i; System.out.print("/ MEAN_T_m01 at col:" + i);};
            			if (sss.equalsIgnoreCase("s_ID")) {i_s_ID = i; System.out.print("/ s_ID at col:" + i);};   
            			if (sss.equalsIgnoreCase("p_res")) {i_p_res = i; System.out.print("/ p_res at col:" + i);};
            			if (sss.equalsIgnoreCase("p_com")) {i_p_com = i; System.out.print("/ p_com at col:" + i);};
            			if (sss.equalsIgnoreCase("p_gre")) {i_p_gre = i; System.out.print("/ p_gre at col:" + i);};
            			if (sss.equalsIgnoreCase("p_pasture")) {i_p_pasture = i; System.out.print("/ p_pasture at col:" + i);};
            			if (sss.equalsIgnoreCase("p_blwood")) {i_p_blwood = i; System.out.print("/ p_blwood at col:" + i);};
            			if (sss.equalsIgnoreCase("p_cfwood")) {i_p_cfwood = i; System.out.print("/ p_cfwood at col:" + i);};
            			if (sss.equalsIgnoreCase("p_mxwood")) {i_p_mxwood = i; System.out.print("/ p_mxwood at col:" + i);};
               			if (sss.equalsIgnoreCase("p_ngrass")) {i_p_ngrass = i; System.out.print("/ p_ngras at col:" + i);};
            			if (sss.equalsIgnoreCase("p_ecotone")) {i_p_ecotone = i; System.out.print("/ p_ecotone at col:" + i);};
               			if (sss.equalsIgnoreCase("d2capital")) {i_d2capital = i; System.out.print("/ d2capital at col:" + i);};
               			if (sss.equalsIgnoreCase("d2town")) {i_d2town = i; System.out.print("/ d2town at col:" + i);};
               			if (sss.equalsIgnoreCase("d2village")) {i_d2village = i; System.out.print("/ d2village at col:" + i);};
               			if (sss.equalsIgnoreCase("d2river")) {i_d2river = i; System.out.print("/ d2river at col:" + i);};
               			if (sss.equalsIgnoreCase("d2lake")) {i_d2lake = i; System.out.print("/ d2lake at col:" + i);};
             			//if (sss.equalsIgnoreCase("t_ad")) {i_t_ad = i; System.out.print("/ t_ad at col:" + i);};
            			//if (sss.equalsIgnoreCase("t_ny")) {i_t_ny = i; System.out.print("/ t_ny at col:" + i);};
            			//if (sss.equalsIgnoreCase("t_la")) {i_t_la = i; System.out.print("/ t_la at col:" + i);};
            			//if (sss.equalsIgnoreCase("i_ad")) {i_i_ad = i; System.out.print("/ i_ad at col:" + i);};
            			//if (sss.equalsIgnoreCase("i_ny")) {i_i_ny = i; System.out.print("/ i_ny at col:" + i);};
            			//if (sss.equalsIgnoreCase("i_la")) {i_i_la = i; System.out.print("/ i_la at col:" + i);};
            			//if (sss.equalsIgnoreCase("d_t_ad")) {i_d_t_ad = i; System.out.print("/ d_t_ad at col:" + i);};
            			//if (sss.equalsIgnoreCase("d_t_ny")) {i_d_t_ny = i; System.out.print("/ d_t_ny at col:" + i);};
            			//if (sss.equalsIgnoreCase("d_t_la")) {i_d_t_la = i; System.out.print("/ d_t_la at col:" + i);};
            			//if (sss.equalsIgnoreCase("d_i_ad")) {i_d_i_ad = i; System.out.print("/ d_i_ad at col:" + i);};
            			//if (sss.equalsIgnoreCase("d_i_ny")) {i_d_i_ny = i; System.out.print("/ d_i_ny at col:" + i);};
            			//if (sss.equalsIgnoreCase("d_i_la")) {i_d_i_la = i; System.out.print("/ d_i_la at col:" + i);};
            			//if (sss.equalsIgnoreCase("t_rs")) {i_t_rs = i; System.out.print("/ t_rs at col:" + i);};
            			//if (sss.equalsIgnoreCase("i_rs")) {i_i_rs = i; System.out.print("/ i_rs at col:" + i);};
            		}
            		
            		System.out.println("/ Total col number: " + line_data.size());
            		System.out.println(">>> reading data");
            		
            	}
            	else if(line_data.size() < 10){	
            		System.out.print(">>> empty line, data size:" + line_data.size());
            	}
            	else{
            		
            		CellStates castate  = new CellStates();
            		//double v_ID = Double.parseDouble((String)line_data.get(i_id));
              		// Double.parseDouble((String)line_data.get(i_landtype));		
              		
              		double c_p_broad = Double.parseDouble((String)line_data.get(i_p_blwood))/100;
            		double c_p_conifer = Double.parseDouble((String)line_data.get(i_p_cfwood))/100;
            		double c_p_mix = Double.parseDouble((String)line_data.get(i_p_mxwood))/100;
            		double c_p_ngras = Double.parseDouble((String)line_data.get(i_p_ngrass))/100;
            		double c_p_ecotone = Double.parseDouble((String)line_data.get(i_p_ecotone))/100;
            		
            		double c_d2lake = Double.parseDouble((String)line_data.get(i_d2lake));
            		double c_d2river = Double.parseDouble((String)line_data.get(i_d2river));
            		double c_slope = Double.parseDouble((String)line_data.get(i_slope));
            		
            		double c_p_res = Double.parseDouble((String)line_data.get(i_p_res))/100;
            		double c_p_com = Double.parseDouble((String)line_data.get(i_p_com))/100;
            		double c_p_gre = Double.parseDouble((String)line_data.get(i_p_gre))/100;
            		
            		double c_urban = c_p_res+ c_p_com+ c_p_gre;
                		
            		double land_type = getLandType(c_p_broad, c_p_conifer, c_p_mix, c_p_ngras, c_p_ecotone, c_urban);
            		
            		castate = updateResStates(land_type);
            		
            		castate.landscape = land_type;
            		castate.p_res = c_p_res;  
            		castate.p_com = c_p_com;  
            		castate.p_gre = c_p_gre;  
            		
            		//int n_blwood= 0, n_cfwood= 0, n_mxwood= 0, n_ngrass= 0, n_ecotone= 0, n_urban =0;
            		
            		if(land_type == 1) n_blwood ++; 
            		else if(land_type == 2) n_cfwood ++;
            		else if(land_type == 3) n_mxwood ++;
            		else if(land_type == 4) n_ngrass ++;
            		else if(land_type == 5) n_ecotone ++;
            		else if(land_type == 6) n_urban ++;
            		else n_other ++;
            		
            		//castate.totoReserviorHost = (Double.parseDouble((String)line_data.get(i_t_rs)));
            		//castate.infectiousReserviorHost = Double.parseDouble((String)line_data.get(i_i_rs));
            		
            		castate.cell_ID = Double.parseDouble((String)line_data.get(i_id));      
            		castate.residents = Double.parseDouble((String)line_data.get(i_pop));    		
            		castate.region_ID = Integer.valueOf(((String)line_data.get(i_s_ID)).replaceAll("[a-zA-Z]", ""));
            		castate.elevation = Double.parseDouble((String)line_data.get(i_elevation));
            		castate.slope = Double.parseDouble((String)line_data.get(i_slope));
            		
            		//castate.deerSuitability = getLandType(c_p_broad, c_p_conifer, c_p_mix, c_p_ngras, c_p_ecotone, c_urban);
            		castate.deerSuitability = estimateReproductionSuitbility(c_p_broad, c_p_conifer, c_p_mix, c_p_ngras, c_p_ecotone);
            		castate.totoReproductionHost = estimateReproductionDensity(c_p_broad, c_p_conifer, c_p_mix, c_p_ngras, c_p_ecotone);
            		castate.resvSuitability =  estimateResSuitability(c_p_broad, c_p_conifer, c_p_mix, c_p_ngras, c_p_ecotone, castate.elevation);
            		castate.tickHabitatScalingFactor = estimateTickHabitatScalingFactor(c_p_broad, c_p_conifer, c_p_mix, c_p_ngras, c_p_ecotone);
            		
            		double p_non_builtup = checkValue(1 - c_p_broad - c_p_conifer - c_p_mix - c_p_ngras - c_p_ecotone - c_p_res); 
            		castate.natural_quality = estimateVisualAttrativeness(c_p_broad, c_p_conifer, c_p_mix, c_p_ngras, c_p_ecotone, p_non_builtup, c_d2lake, c_d2river, c_slope);
            		
            		n_toto_nq = n_toto_nq + castate.natural_quality;
            		
            		//castate.totoLivestockHost = Double.parseDouble((String)line_data.get(i_livestockpopulation));
            		//castate.deerSuitability = (Double.parseDouble((String)line_data.get(i_deersuitabiliy)))/100;
            		//castate.totoReproductionHost = Double.parseDouble((String)line_data.get(i_deerpopulation));
            		 
            		//castate.totoAdultTick = Double.parseDouble((String)line_data.get(i_t_ad));
            		//System.out.println(castate.totoAdultTick);
            		//castate.totoNymphTick = Double.parseDouble((String)line_data.get(i_t_ny)); 
            		//castate.totoLarvaTick = Double.parseDouble((String)line_data.get(i_t_la));
            		//castate.infectiousAdultTick = Double.parseDouble((String)line_data.get(i_i_ad));
            		//castate.infectiousNymphTick = Double.parseDouble((String)line_data.get(i_i_ny)); 
            		//castate.infectiousLarvaTick = Double.parseDouble((String)line_data.get(i_i_la));
            		//castate.d_totoAdultTick = Double.parseDouble((String)line_data.get(i_d_t_ad));
            		//castate.d_totoNymphTick = Double.parseDouble((String)line_data.get(i_d_t_ny)); 
            		//castate.d_totoLarvaTick = Double.parseDouble((String)line_data.get(i_d_t_la));
            		//castate.d_infectiousAdultTick = Double.parseDouble((String)line_data.get(i_d_i_ad));
            		//castate.d_infectiousNymphTick = Double.parseDouble((String)line_data.get(i_d_i_ny)); 
            		//castate.d_infectiousLarvaTick = Double.parseDouble((String)line_data.get(i_d_i_la));	

         		
            		//System.out.println(land_type + ";;" + c_p_broad + ";;" + c_p_conifer + ";;" + c_p_igras + ";;" + c_p_ngras + ";;" + c_p_heath);
            		        		
            		//System.out.println(land_type + ";;" +castate.tickHabitatScalingFactor);
            		
            		/*
            		castate.totoReserviorHost = (getResDen(1.0)*c_p_broad + getResDen(2.0)*c_p_conifer 
            				+ getResDen(3.0)*c_p_igras + getResDen(4.0)*c_p_ngras 
            				+ getResDen(heathType)*c_p_heath)/p_sum; // + getSfactor(land_type)*p_rest;
 					
 					castate.infectiousReserviorHost = castate.totoReserviorHost*0.2;*/
 					/*
            		castate.resvSuitability // + getResSuitability(land_type)*p_rest;;
            		     		
            		
            		//System.out.println(land_type + ";;" + castate.resvSuitability);
            		
            		if (land_type == 1 || land_type == 2){
            			castate.tickHabitatScalingFactor = getSfactor(land_type);  
                		
            			//System.out.println(castate.tickHabitatScalingFactor);
            		}
            		else{
            			double sf_major_land = getSfactor(land_type);
            			double c_p_broad = Double.parseDouble((String)line_data.get(i_p_broad))/100;
                		double c_p_conifer = Double.parseDouble((String)line_data.get(i_p_conifer))/100;
                		castate.tickHabitatScalingFactor = sf_major_land * (1-c_p_broad-c_p_conifer) + getSfactor(1.0)*c_p_broad + getSfactor(2.0)*c_p_conifer; 
                		//System.out.println(castate.tickHabitatScalingFactor - sf_major_land);
            		}*/
          		
            		//castate.initialReproductionHost = castate.totoReproductionHost;
            		
            		idData.add((double)castate.cell_ID);

            		castate.n_x = Integer.valueOf(((String)line_data.get(i_x)).replaceAll("[a-zA-Z]", ""));
            		castate.n_y = Integer.valueOf(((String)line_data.get(i_y)).replaceAll("[a-zA-Z]", ""));
            		
                	//System.out.print(">>> ["+ castate.cell_ID +"] loading temperature");
                	
                	double[] temp = new double[12];          	
                	for(int ww=0; ww<12; ww++){
                		int new_ww = ww + i_1stmonth;
                		temp[ww] = Double.parseDouble((String)line_data.get(new_ww));
                		//System.out.println("/ month " + "ww=" + temp[ww] );
                	}
                	
                	castate.temperature = temp;
                	
                	CAMPCA ca = new CAMPCA();
                	ca.state = castate;  	
                	CAMPContextBuilder.mainContext.add(ca);   
                	CAMPContextBuilder.mainGrid.moveTo(ca, ca.state.n_x, ca.state.n_y);
                                      
            	}           	            
                //System.out.println("xxxxx line break xxxxx");
                row++;
            }
            
            bufRdr.close();
           
            int n_cell = row - 1;
            
            //n_blwood= 0, n_cfwood= 0, n_mxwood= 0, n_ngrass= 0, n_ecotone= 0, n_urban =0
            System.out.println(">>>>> land cover summary <<<<<" );
            System.out.println(">> broadleaf woodland : " + n_blwood + " cells");
            System.out.println(">> coniferous woodland : " + n_cfwood + " cells");
            System.out.println(">> mixed woodland :  " + n_mxwood + " cells");
            System.out.println(">> natural grassland :  " + n_ngrass + " cells");
            System.out.println(">> ecotone :  " + n_ecotone + " cells");
            System.out.println(">> urban :  " + n_urban + " cells");
            System.out.println(">> other :  " + n_other + " cells");
            
            System.out.println(">>>>> total natural quility :  " + n_toto_nq);
    		
            System.out.println(">> system starts with " + n_cell + " cells");
			
		} catch (IOException | RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
    }
    
	private double checkValue(double value){
		if (value > 1e-8) return value;
		else return 0;
	}
	
    private CellStates updateResStates(double land_type){
    	
    	CellStates state1 = new CellStates();
    	//state1.landscape = land_type;
    	
    	if (land_type == 1){ // broadleaf woodland
    		state1.totoReserviorHost = 6000;
    		state1.infectiousReserviorHost = 3000;
    		state1.totoAdultTick = 20000; 
    		state1.totoNymphTick = 100000; 
    		state1.totoLarvaTick = 1000000;
    		state1.infectiousNymphTick = 20000;
    	}
    	else if (land_type == 2){ //Coniferous woodland
    		state1.totoReserviorHost = 5975;
    		state1.infectiousReserviorHost = 3000;
    		state1.totoAdultTick = 20000; 
    		state1.totoNymphTick = 100000; 
    		state1.totoLarvaTick = 1000000;
    		state1.infectiousNymphTick = 20000;
    	}
    	else if (land_type == 3){ // Mixed woodland
    		state1.totoReserviorHost = 5975;
    		state1.infectiousReserviorHost = 3000;
    		state1.totoAdultTick = 20000; 
    		state1.totoNymphTick = 100000; 
    		state1.totoLarvaTick = 1000000;
    		state1.infectiousNymphTick = 20000;
    	}
    	else if (land_type == 4){ // Natural grassland
    		state1.totoReserviorHost = 5621;
    		state1.infectiousReserviorHost = 1500;
    	}
    	else if (land_type == 5){ // Ecotone
    		state1.totoReserviorHost = 4445;
    		state1.infectiousReserviorHost = 1300;
    	}
    	else if (land_type == 6){ // Urban
    		state1.totoReserviorHost = 0;
    		state1.infectiousReserviorHost = 0;
    	}
    	else{
    		state1.totoReserviorHost = 0;
    		state1.infectiousReserviorHost = 0;
    	}
    	return state1;
    }
    
    /* ASCII raster header: 
	ncols         527
	nrows         457
	xllcorner     -6.458949628715
	yllcorner     54.599434974455
	cellsize      0.008939222765
	NODATA_value  -9999
    */
    
    private double getLandType(double p_broad, double p_conifer, double p_mix, double p_ngras, double p_ecotone, double p_urban ){
    	
    	double lt = 0;
       
    	double p_other = 1 - p_broad - p_conifer - p_mix - p_ngras - p_ecotone;
        
    	if(p_urban > 0.1){
    		lt = 6;
    	}
    	else{
    		if(p_other < 0.5){
            	
            	ArrayList<Double> l = new ArrayList<Double>();
        		
                l.add(p_broad);
                l.add(p_conifer);
                l.add(p_mix);
                l.add(p_ngras);
                l.add(p_ecotone);
                //l.add(p_urban);
                
                Collections.sort(l);
                
                double max = l.get(l.size() - 1);
             
                if (p_broad == max){ lt = 1;}
                else if (p_conifer == max){ lt = 2;}
                else if (p_mix == max){ lt = 3;}
                else if (p_ngras == max){ lt = 4;}
                else if (p_ecotone == max){ lt = 5;}
                //else if (p_urban == max){ lt = 6;}
                else {lt = 0;}        	
            }
    	}		     
        return lt; 	
    }
    
    private double estimateReproductionDensity(double p_broad, double p_conifer, double p_mix, double p_ngras, double p_ecotone){
    	
    	double den = 0;
    	
    	den = p_broad * 5.85 + p_conifer * 6.3 + p_mix * 6.5;
    	//den = p_broad * 2.12 + p_conifer * 3 + p_mix * 2.6 + 2.43;
    	
        return den; 	
    }
  
    private double estimateReproductionSuitbility(double p_broad, double p_conifer, double p_mix, double p_ngras, double p_ecotone){
  	
  		double suit = 0;
  	
  		suit = p_broad * 0.9 + p_conifer * 0.97 + p_mix * 1;
    
  		return suit; 	
    }
    
    private double estimateResSuitability(double p_broad, double p_conifer, double p_mix, double p_ngras, double p_ecotone, double elev){
      	
  		double suit = 0, suit1 =0, suit2=0;
  	
  		suit1 = p_broad * 1 + p_conifer * 0.692 + p_mix * 0.769 + p_ngras*0.538 + p_ecotone*0.692;
  		
  		if (elev <= 300) {suit2 = 0.96;}
  		else if (elev <= 500) {suit2 = 0.88;}
  		else if (elev <= 700) {suit2 = 0.68;}
  		else if (elev <= 900) {suit2 = 1;}
  		else {suit2 = 0.48;}
  		
  		suit = suit1*suit2;
  		
  		return suit; 	
    }
    
    private double estimateTickHabitatScalingFactor(double p_broad, double p_conifer, double p_mix, double p_ngras, double p_ecotone){
      	
  		double suit = 0;
  	
  		suit = (getSfactor(1.0)*p_broad + getSfactor(2.0)*p_conifer + getSfactor(3.0)*p_mix + getSfactor(4.0)*p_ngras + getSfactor(5.0)*p_ecotone)/
  				(p_broad + p_conifer + p_mix + p_ngras + p_ecotone); 
    
  		return suit; 	
    }
    
    private double getSfactor(double landType){
		
		double sf;
		//System.out.println(landType);
		if (landType == 1.0) sf = 1;
		else if (landType == 2.0) sf = 1.25;
		else if (landType == 3.0) sf = 1.1;
		else if (landType == 4.0) sf = 1.5;
		else if (landType == 5.0) sf = 1.2;
		else if (landType == 6.0) sf = 100;
		else sf = 100;
		//System.out.println(sf);
		return sf;
		
	}
    
    private double estimateVisualAttrativeness(double p_broad, double p_conifer, double p_mix, double p_ngras, double p_ecotone, double p_non_builtup, double d2lake, double d2river, double slope){
      	
  		double V= 0, U = 0, A = 1, T = 1;
  	
  		U = 9 * p_broad + 6.5 * p_conifer + 8 * p_mix + 5 * p_ngras + 6.5 * p_ecotone + 4.5 * p_non_builtup; 
  		
  		if (d2lake < 1) d2lake = 1;
  		if (d2river < 1) d2river = 1;
  		
  		A = (1 + 0.5 * (1/(d2lake * d2lake))) * (1 + 0.45 * (1/(d2river * d2river)));
  		
  		if(slope<=3) T = 9.3;
  		else if (slope<=10) T = 9.8;
  		else if (slope<=20) T = 10.3;
  		else T = 10.6;
  		
  		V = U*A*T;
  		
  		//System.out.println(V);
  		return V; 	
	
    }

} 