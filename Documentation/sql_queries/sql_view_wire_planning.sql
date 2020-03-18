SELECT 
	project,		
	wire_stock,		
	card_number,	
	wire_no,
	SUM(planned_qty) AS planned_qty,	
	SUM(bundle_demand) AS bundle_demand
	-- Colonne wire_demand_rounded = planned_qty * bundle_demand à ajouter danss jtable
	FROM	
(
SELECT 
	project,		
	stock AS wire_stock,		
	card_number,	
	wire_no,
	SUM(planned_qty) AS planned_qty,
	CASE
		WHEN (planned_qty - stock) < 0 THEN 0
		ELSE (planned_qty - stock)
	END AS wire_demand,
	CASE
		WHEN CEIL((planned_qty - stock) / bundle_qty)::integer < 0 THEN 0
		ELSE CEIL((planned_qty - stock) / bundle_qty)::integer
	END AS bundle_demand	
	FROM (
	SELECT 
		wc.project,		
		cs.workplace,
		pp.harness_part, 
		pp.internal_part, 					
		wc.card_number,	
		wc.wire_no,
		wc.stock,			
		pp.planned_qty,
		wc.bundle_qty
		FROM public.production_plan pp	
		INNER JOIN public.config_ucs cs ON pp.internal_part = cs.supplier_part_number
		INNER JOIN public.wire_config wc ON pp.internal_part = wc.internal_part
		
	GROUP BY wc.project, wc.wire_no, wc.card_number, wc.stock, pp.planned_qty, cs.workplace,pp.harness_part, 
		pp.internal_part, wc.bundle_qty
	ORDER BY wc.wire_no
	) AS T
GROUP BY project, wire_no, planned_qty, stock, card_number, bundle_qty	
) AS WIRE_DEMAND
GROUP BY project,		
	wire_stock,		
	card_number,	
	wire_no
ORDER BY wire_no
;