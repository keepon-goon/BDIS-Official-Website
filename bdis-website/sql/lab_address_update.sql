-- 同步现有站点信息中的旧地址，保留大学名称等其他内容。
UPDATE lab_info
SET address = REPLACE(address, '博园4号楼305', '汇智楼502')
WHERE address LIKE '%博园4号楼305%';
