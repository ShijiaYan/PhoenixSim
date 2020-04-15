package NASA.ErrorCoding;

import flanagan.interpolation.LinearInterpolation;

public class BlockTurbo extends AbstractCoding {

	
	
	@Override
	public double getCodingGain(double BER) {
		double netCodingGain = Math.abs(getUncodedOSNR(BER)-getCodedOSNR(BER)) ;
		return netCodingGain ;
	}

	@Override
	public double getUncodedOSNR(double BER){
		double logBER = Math.log10(BER) ;
		double[] log_BER = {-1.9844128376758511933530826354399, -1.9847214842509810495130295748822, -1.9977059263771153041489014867693, -2.0490796166581421999808299005963, -2.1259858287921176156487490516156, -2.1771892313075547065182036021724, -2.2413451469930798509722080780193, -2.2925911214499148371714909444563, -2.3822689160030403598966586287133, -2.4334616755331275328444462502375, -2.48477150790206025021689129062, -2.5488422797047878276543997344561, -2.6640313101401638107290636980906, -2.6896809048319560986328724538907, -2.7280914389575467993154234136455, -2.7921196388188782577799429418519, -2.8690045649821556139613676350564, -2.9712410822474435434514816734008, -2.9841190945200857242980418959633, -3.0097367602558273702584301645402, -3.0608869478445193124116485705599, -3.0865046135802627347288762393873, -3.1377505880370986091065788059495, -3.1889007756257887749029578117188, -3.3295797559727153824837841966655, -3.3552187076791568642875063233078, -3.3807831584881515851748190470971, -3.4703758091584839817755891999695, -3.5342975791663242546292167389765, -3.6111186474175047678158989583608, -3.6750510604106940704127737262752, -3.764601139139626795326876163017, -3.866933443273058657041474361904, -3.9692444614357906829127387027256, -4.07158740855457157437058413052, -4.1867445100338978036802473070566, -4.2761881589093375666266183543485, -4.3400886429464780036369120352902, -4.3912707594912196995551312284078, -4.4423783751385128582001016184222, -4.4680173268449543400038237450644, -4.5191781574189953119002893799916, -4.5831105704121855026755838480312, -4.6597507079124280693349646753632, -4.6981612420380187700175156351179, -4.8004509742300527364022855181247, -4.8643514582671949497694185993169, -4.9282306563336373272932178224437, -4.9921417833561294585820178326685, -5.0560741963493187611788926005829, -5.1072030979673117556671968486626, -5.1838538784529042402482446050271, -5.2222324836224460753442144778091, -5.2988619781373405004387677763589, -5.3244902868584320643208229739685, -5.4139126497631746559591192635708, -5.4905527872634163344400803907774, -5.592810590499403211595108587062, -5.7206009155883377204077078204136, -5.7972410530885802870670886477455, -5.8739344055155706669779647199903, -5.9378136035820130445017639431171, -6.0017034446338053399472300952766, -6.0783222961633507352985361649189, -6.2699492473773306144835260056425, -6.3593928962527703774298970529344, -6.4232827373045635610537829052191, -6.4999015888341080682266692747362, -6.5765523693196996646292973309755, -6.6276919139230425770392685080878, -6.7170823478717354149125640105922, -6.7937224853719788697503645380493, -6.8831448482767205732102411275264, -6.9981103760197598262493556831032, -7.1002936783583008306663941766601, -7.1641622334393950666253658710048, -7.2535845963441341055499833601061, -7.3429963162635285556234521209262, -7.4452328335288155969351464591455, -7.5218729710290581635945272864774, -7.624024344411552078781824093312, -7.7006538389264456156979576917365, -7.7773259053827361597654999059159, -7.8794772787652300749527967127506, -7.930606180383222181262681260705, -7.9944640924789656111215663258918, -8.0710829440085110064728723955341, -8.2115597076337998316830635303631, -8.2754176197295468142556273960508, -8.4542410595683286089752073166892, -8.5180883286787238972692648530938, -8.6585970212600606998876173747703, -8.7607590376279027566397417103872, -8.837377889157446375634208379779, -8.9395292625399420671783445868641, -9.0033978176210354149588965810835, -9.1055491910035293301461933879182, -9.1694177460846209015699059818871, -9.2205466477026156724150496302173, -9.335427031562860022972927254159, -9.412088455033801537297222239431, -9.5014682459971453454272705130279, -9.5908480369604873772004793863744, -9.6802916858359289165036898339167, -11} ;
		double[] OSNR_dB = {7.6526408859110839344452870136593, 7.837417302221904691350573557429, 7.971235104680321370551610016264, 8.1688114847045554256510513368994, 8.372787846585495685758360195905, 8.4684186176106575061339754029177, 8.6787523894069078522761628846638, 8.7998695626818381754219444701448, 9.010231715772352600879457895644, 9.0994908862350705192056921077892, 9.2588376628846535965067232609726, 9.4181986301813687134654173860326, 9.6222175640037050925457151606679, 9.6987051520472764565283796400763, 9.7752069307379798601687070913613, 9.9090814957849246980003954377025, 10.100314656540980706722621107474, 10.189630589592230336393186007626, 10.259732386426225758668806520291, 10.317105172782468969217006815597, 10.380877940995418384773074649274, 10.438250727351661595321274944581, 10.559367900626591918467056530062, 10.62314066883954311037996376399, 10.808073182268820744411641499028, 10.878189169749948206344924983568, 10.90370395329398078843041730579, 11.063093301884958208347597974353, 11.133251861307483565610709774774, 11.286255418688888596534525277093, 11.362785578673854303133339271881, 11.496688525015064996637192962226, 11.643348863128293757540632213932, 11.777266000116632937988470075652, 11.930297938792305600941290322226, 12.11520207092731737930080271326, 12.185389011644106815879240457434, 12.242804369941747921757269068621, 12.3256919398420254907478010864, 12.363978305805206403533702541608, 12.4340942932863356418238254264, 12.504238662061727183072434854694, 12.580768822046694666028088249732, 12.625455169866583560178696643561, 12.701956948557285187462184694596, 12.823130884420743669238618167583, 12.880546242718382998759807378519, 12.925218399891141629609592200723, 12.989005358751223084823323006276, 13.065535518736190567778976401314, 13.116565085824255731949961045757, 13.167623034206586751793111034203, 13.225010011209963778355813701637, 13.2633247584674123231707199011, 13.327069145386095883054622390773, 13.384512884978002844604816345964, 13.429199232797891738755424739793, 13.531258366974023843454233428929, 13.639717483006863929517749056686, 13.684403830826751047311518050265, 13.760948181458850569924834417179, 13.805620338631607424417779839132, 13.856664096366808180960106255952, 13.888607243061811047368792060297, 14.016252114017639485155086731538, 14.086439054734428921733524475712, 14.137482812469627901919011492282, 14.169425959164630768327697296627, 14.220483907546963564527686685324, 14.277885075197469078034373524133, 14.316214013102051438863782095723, 14.360900360921938556657551089302, 14.418344100513845518207745044492, 14.48855942252490081045834813267, 14.546017352763938035309365659487, 14.584317909374254540466608887073, 14.641761648966157949303124041762, 14.692833787995622785160776402336, 14.782149721046870638474501902238, 14.826836068866757756268270895816, 14.865179197418472156755342439283, 14.903493944675918925213409238495, 14.967295094183132420084803015925, 15.00563822273484326785819575889, 15.056667789822910208386019803584, 15.088596745870782811493882036302, 15.120539892565787454259407240897, 15.184411995308666476489634078462, 15.21634095135653730324065691093, 15.318485229415463422242282831576, 15.344042584900895676014442869928, 15.427029489331095746251776290592, 15.471744218445250496074550028425, 15.50368736514025691519691463327, 15.542030493691969539327146776486, 15.580331050302282491770711203571, 15.618674178853995115900943346787, 15.656974735464308068344507773872, 15.708004302552373232515492418315, 15.727246820063893295582602149807, 15.78467636900866466476145433262, 15.816633706350804899898321309593, 15.848591043692939805964670085814, 15.918777984409729242543107829988, 15.944349530542289983259252039716} ;		
		LinearInterpolation linBER = new LinearInterpolation(log_BER, OSNR_dB) ;
		double OSNR_uncoded_dB = linBER.interpolate(logBER) ;
		return OSNR_uncoded_dB ;
	}
	
	@Override
	public double getCodedOSNR(double BER){
		double logBER = Math.log10(BER) ;
		double[] log_BER = {-1.9487364799946185200951731530949, -2.0216703168273468804727599490434, -2.0217613829388199064851505681872, -2.0461367454429879586541574099101, -2.1069284340793679177750163944438, -2.1313139150403657140486757270992, -2.1678010703703911588036135071889, -2.2285927590067711179244724917226, -2.252927647683618417318029969465, -2.277262536360463940354748046957, -2.3137395732336578646481939358637, -2.3623688767600317106598595273681, -2.447374032591293158134249097202, -2.520247158683041277527081547305, -2.6174045811674826467196908197366, -2.6903181810865497425311332335696, -2.7631913071782960855671262834221, -2.8360644332700442049599587335251, -2.9331915003839945654817711329088, -3.0545725085201498316678225819487, -3.1517198125477605685773596633226, -3.2852227319666029714539945416618, -3.3823295621668938437665019591805, -3.515812244672074093898572755279, -3.6128988379587054780017751909327, -3.7463916389207181367737575783394, -3.8798439660554082664134512015153, -4.0133266485605902929023613978643, -4.1832054202843238499553990550339, -4.3287897771585335249255876988173, -4.5229326083613008435690971964505, -4.6927810247145433919513379805721, -4.862649677981446316721303446684, -5.0567621538137244030508554715198, -5.2144987773411113352040047175251, -5.3843471936943538835862455016468, -5.5299416690253933026610866363626, -5.6148557587451843886583446874283, -5.78470417509842693704058547155, -5.9181767391467783312464234768413, -6.0759032442173337429380808316637, -6.2093656898088545048608466458973, -6.3549298097694029152648909075651, -6.5126765517536187033442729443777, -6.6825249681068621399049334286246, -6.7552969096303057128238833684009, -6.8645155326564513131870626239106, -7.0101201264443222527233956498094, -7.0950240977072818182591618096922, -7.2648623956036946225367501028813, -7.4347108119569380590974105871283, -7.5803154057448072222769042127766, -7.750173940554880402942217187956, -7.8957582974290883015555664314888, -8.0292106245637793193736797547899, -8.2961152788331613550099064013921, -8.4295777244246821169326722156256, -8.5387659920803375968034742982127, -8.6843604674113770158783154329285, -8.878462824786826246281634666957, -9.1939057164711055492034574854188, -9.3152361323231094303309873794205, -9.5336126676344221664294309448451, -9.6670649947691131842475442681462, -9.8126493516433228592177329119295, -9.9824977679965645194215539959259, -10.140214154610289298830139159691, -10.346458660182085154133346804883, -10.479910987316774395594620727934, -10.601241403168780053078990022186, -10.72254146365029114917888364289, -10.856014027698643431563141348306, -11} ;
		double[] OSNR_dB = {4.8839929801721426727567632042337, 4.9688800873403877744749479461461, 5.0233983327419879927333568048198, 5.0900587263399366122484934749082, 5.1688747594100057369814749108627, 5.2415927358303546768070191319566, 5.2961514550592774241977167548612, 5.374967488129347437109117890941, 5.4173975504376956635610440571327, 5.4598276127460430018345505231991, 5.508328749152564540736420894973, 5.5689585424796614887554824235849, 5.6175136439892785489291782141663, 5.6660552542231208406064979499206, 5.7267390126533124217189651972149, 5.7995109541767551064594954368658, 5.8480525644105991744936545728706, 5.8965941746444414661709743086249, 5.9391051846074311981737992027774, 5.9755855942995692586805489554536, 6.0302117699073605194826086517423, 6.0606480880528703636400678078644, 6.0910439323710630077357564005069, 6.109365084871772211272400454618, 6.1276457635451624383904345449992, 6.1520244988682746267727452504914, 6.1521729029017855339134257519618, 6.1704940554024965138069092063233, 6.1888556817305291346542617247906, 6.1890175770398139221128985809628, 6.2074061859193943035961638088338, 6.2075950637802259635122936742846, 6.2198991072858582640492386417463, 6.2201149676982376846012812166009, 6.2324055199280952166418501292355, 6.2325943977889277647363996948116, 6.2388138759206128725054441019893, 6.2328507320286288262423113337718, 6.2330396098894604861584411992226, 6.2453031795677720339199368027039, 6.2515361489752310220069375645835, 6.2577421358311413612796059169341, 6.2457888654956263962958473712206, 6.2641370005478851368252435349859, 6.2643258784087167967413734004367, 6.2522916604185594380282964266371, 6.2705858303677217691074474714696, 6.2828628913218089735437388299033, 6.2708421646074228306133591104299, 6.2649734596458550583975011250004, 6.2651623375066876064920506905764, 6.2774393984607721463930829486344, 6.2836858591440059029764597653411, 6.2838477544532906904350966215134, 6.2839961584868007093973574228585, 6.2842929665538225236787184257992, 6.2904989534097328629513867781498, 6.2906203748916960094561545702163, 6.2968398530233793408683595771436, 6.2909981306133602174668340012431, 6.2974064866058752087951688736212, 6.3035989821860152204635596717708, 6.3038418251499415134730952559039, 6.3039902291834524206137757573742, 6.3041521244927372080724126135465, 6.3043410023535688679885424789973, 6.3045163889386275357651356898714, 6.3047457406267817248135543195531, 6.3048941446602917437758151208982, 6.3110866402404290909089468186721, 6.2991063873533637007540164631791, 6.3113699570316779130507711670361, 6.3115048697894149398734953138046} ;
		LinearInterpolation linBER = new LinearInterpolation(log_BER, OSNR_dB) ;
		double OSNR_coded_dB = linBER.interpolate(logBER) ;
		return OSNR_coded_dB ;
	}

	@Override
	public String getCodingName() {
		return "BlockTurbo" ;
	}

}