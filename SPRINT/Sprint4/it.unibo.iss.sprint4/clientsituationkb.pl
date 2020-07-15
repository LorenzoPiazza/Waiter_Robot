%===========================================
% clientsitationkb.pl
%===========================================

%% ------------------------------------------ 
%% Client Situation
%% -----------------------------------------
%% 
%% client(CID, STATE, INIT_TIME)
%% CID: clientId
%% STATE: consulting | consuming | waiting_preparation
%% INIT_TIME: time in which the client CID is enter in the current STATE


showClientSituation(L1, L2) :- findall( [C,S,T], client(C,S,T), L1),
	stdout <- println( clientsituationkb_clients(L1) ),
	findall( [C1,T1], elapsed(C1,T1), L2),
	stdout <- println( clientsituationkb_elapsed(L2) ).


clientAndTimesToControl(L) :- findall( [CID, T], client(CID,consulting,T), L1),  findall( [CID, T], client(CID,consuming,T), L2), append(L1,L2,L).

getClientOverTime(_, _, [], []).
getClientOverTime(MAXST, CURTIME, [[A,B]|T], [A|S]) :- N is CURTIME-B, N >= MAXST, !, getClientOverTime(MAXST, CURTIME, T, S).
getClientOverTime(MAXST, CURTIME, [_|T], S) :- getClientOverTime(MAXST, CURTIME, T, S).


