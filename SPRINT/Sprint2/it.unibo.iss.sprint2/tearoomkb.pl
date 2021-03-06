%===========================================
% tearoomkb.pl
%===========================================

%% ------------------------------------------ 
%% Positions
%% ------------------------------------------ 
pos( barman,       5, 0 ).
pos( teatable1,    2, 2 ).	%% Waiter not optimized
pos( teatable2,    4, 2 ).	%% Waiter not optimized
pos( entrancedoor, 1, 4 ).
pos( exitdoor,     5, 4 ).


%% ------------------------------------------ 
%% Teatables
%% ------------------------------------------ 
%% busy(CID)		(occupied by client CID)
%% dirty	  	(free but not clean)
%% tableclean 		(free and clean)	

teatable( 1, tableclean ).
teatable( 2, tableclean ).


numfreetables(N) :-
	findall( N,teatable( N,tableclean ), NList),
	%% stdout <- println( tearoomkb_numfreetables(NList) ),
	length(NList,N).

stateOfTeatables( [teatable1(V1),teatable2(V2)] ) :-
	teatable( 1, V1 ),
	teatable( 2, V2 ).

releaseTable(N)	:-
	stdout <- println( tearoomkb_engageTable(N) ),
	retract( teatable( N, busy(CID ) ),
	!,
	assert( teatable( N, dirty ) ).
releaseTable(N).

engageTable(N, CID)	 :-
	stdout <- println( tearoomkb_engageTable(N) ),
	retract( teatable( N, tableclean ) ),
	!,
	assert( teatable( N, busy(CID) ) ).
engageTable(N, CID).	
	
	
cleanTable(N)	 :-
	stdout <- println( tearoomkb_cleanTable(N) ),
	retract( teatable( N, dirty ) ),
	!,
	assert( teatable( N, tableclean ) ).
cleanTable(N).	

%% ------------------------------------------ 
%% Waiter
%% ------------------------------------------ 

%%  rest(X,Y)
%%	serving_client( CLIENTID )
%%	cleaning( table(N) )

waiter( rest(0,0) ).	

%% ------------------------------------------ 
%% ServiceDesk
%% ------------------------------------------ 
%% idle
%% preparing( CLIENTID )
%% ready( CLIENTID )

servicedesk( idle ).

%% ------------------------------------------ 
%% Room as a whole
%% ------------------------------------------ 
roomstate(  waiter(S), stateOfTeatables(V), servicedesk(D) ):-
	 waiter(S), stateOfTeatables(V), servicedesk(D).
	 
	