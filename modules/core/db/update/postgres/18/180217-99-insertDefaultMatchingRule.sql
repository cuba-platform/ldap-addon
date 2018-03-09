INSERT INTO LDAP_MATCHING_RULE_ORDER(id,version,create_ts,created_by,order_) values('ff2ebe74-3836-465b-9185-60141a6a0548',0,now(),'admin',2147483647);
INSERT INTO LDAP_MATCHING_RULE_STATUS(id,version,create_ts,created_by,is_active) values('ff2ebe74-3836-465b-9185-60141a6a0548',0,now(),'admin',true);

INSERT INTO LDAP_MATCHING_RULE (id,version,create_ts,created_by,rule_type,description,is_terminal_rule,is_override_existing_roles,
is_override_exist_access_grp,matching_rule_order_id,matching_rule_status_id)
values
('ff2ebe74-3836-465b-9185-60141a6a0548',0,now(),'admin','DEFAULT','Default rule',false,false,false,'ff2ebe74-3836-465b-9185-60141a6a0548','ff2ebe74-3836-465b-9185-60141a6a0548');
