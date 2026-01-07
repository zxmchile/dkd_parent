package com.dkd.manage.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.dkd.common.constant.DkdContants;
import com.dkd.common.utils.DateUtils;
import com.dkd.common.utils.uuid.UUIDUtils;
import com.dkd.manage.domain.Channel;
import com.dkd.manage.domain.Node;
import com.dkd.manage.domain.VmType;
import com.dkd.manage.mapper.ChannelMapper;
import com.dkd.manage.mapper.NodeMapper;
import com.dkd.manage.mapper.VmTypeMapper;
import com.dkd.manage.service.IChannelService;
import com.dkd.manage.service.INodeService;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForJapaneseDate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dkd.manage.mapper.VendingMachineMapper;
import com.dkd.manage.domain.VendingMachine;
import com.dkd.manage.service.IVendingMachineService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 设备管理Service业务层处理
 * 
 * @author chile
 * @date 2026-01-03
 */
@Service
public class VendingMachineServiceImpl implements IVendingMachineService 
{
    @Autowired
    private VendingMachineMapper vendingMachineMapper;

    @Autowired
    private VmTypeMapper vmTypeMapper;

    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private IChannelService iChannelService;

    @Autowired
    private INodeService iNodeService;

    /**
     * 查询设备管理
     * 
     * @param id 设备管理主键
     * @return 设备管理
     */
    @Override
    public VendingMachine selectVendingMachineById(Long id)
    {
        return vendingMachineMapper.selectVendingMachineById(id);
    }

    /**
     * 查询设备管理列表
     * 
     * @param vendingMachine 设备管理
     * @return 设备管理
     */
    @Override
    public List<VendingMachine> selectVendingMachineList(VendingMachine vendingMachine)
    {
        return vendingMachineMapper.selectVendingMachineList(vendingMachine);
    }

    /**
     * 新增设备管理
     * 
     * @param vendingMachine 设备管理
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertVendingMachine(VendingMachine vendingMachine)
    {
        // 1.新增设备信息
        // 1.1 生成设备编号
        String innerCode = UUIDUtils.getUUID();
        vendingMachine.setInnerCode(innerCode);
        // 1.2 设置设备容量
        VmType vmType = vmTypeMapper.selectVmTypeById(vendingMachine.getVmTypeId());
        vendingMachine.setChannelMaxCapacity(vmType.getChannelMaxCapacity());
        // 1.3 设置详细地址
        Node node = nodeMapper.selectNodeById(vendingMachine.getNodeId());
        BeanUtils.copyProperties(node, vendingMachine,"id");
        vendingMachine.setAddr(node.getAddress());
        // 1.4 设置创建时间
        vendingMachine.setCreateTime(DateUtils.getNowDate());
        vendingMachine.setUpdateTime(DateUtils.getNowDate());
        // 1.5 设置设备状态
        vendingMachine.setVmStatus(DkdContants.VM_STATUS_NODEPLOY);
        int result = vendingMachineMapper.insertVendingMachine(vendingMachine);
        // 2.新增货道
        // 2.1 创建货道List
        List<Channel> channelList = new ArrayList<>();
        for (int i = 0; i < vmType.getVmRow(); i++) { // 行
            for (int j = 0; j < vmType.getVmCol(); j++) { // 列
                Channel channel = new Channel();
                channel.setChannelCode(i+"-"+j);
                channel.setVmId(vendingMachine.getId()); // MyBatis返回的ID
                channel.setInnerCode(innerCode);
                channel.setMaxCapacity(vmType.getChannelMaxCapacity());
                channel.setCreateTime(DateUtils.getNowDate());
                channel.setUpdateTime(DateUtils.getNowDate());
                channelList.add(channel);
            }
        }
        int result2 = iChannelService.insertBatchChannel(channelList);
        return result;
    }

    /**
     * 修改设备管理
     * 
     * @param vendingMachine 设备管理
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateVendingMachine(VendingMachine vendingMachine)
    {
        if (vendingMachine.getNodeId()!=null) {
            Node node = iNodeService.selectNodeById(vendingMachine.getNodeId());
            vendingMachine.setAddr(node.getAddress());
            vendingMachine.setBusinessType(node.getBusinessType());
            vendingMachine.setRegionId(node.getRegionId());
            vendingMachine.setPartnerId(node.getPartnerId());
            vendingMachine.setUpdateTime(DateUtils.getNowDate());
        }
        int result = vendingMachineMapper.updateVendingMachine(vendingMachine);
        return result;
    }

    /**
     * 批量删除设备管理
     * 
     * @param ids 需要删除的设备管理主键
     * @return 结果
     */
    @Override
    public int deleteVendingMachineByIds(Long[] ids)
    {
        return vendingMachineMapper.deleteVendingMachineByIds(ids);
    }

    /**
     * 删除设备管理信息
     * 
     * @param id 设备管理主键
     * @return 结果
     */
    @Override
    public int deleteVendingMachineById(Long id)
    {
        return vendingMachineMapper.deleteVendingMachineById(id);
    }
}
