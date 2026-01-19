## 在 hex_uuid_block 前插入 hex_uuid_bit
$data modify storage wing_kirin:ram math.hex_uuid_block set value "$(hex_uuid_bit)$(hex_uuid_block)"
