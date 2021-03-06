package com.example.demo.user.biz.controller;

import com.example.demo.common.core.annotation.SysLogAn;
import com.example.demo.common.core.response.AirResult;
import com.example.demo.common.core.utils.Constant;
import com.example.demo.common.core.utils.PageUtils;
import com.example.demo.common.core.validator.ValidatorUtils;
import com.example.demo.user.api.entity.SysRole;
import com.example.demo.user.api.request.RoleAddDTO;
import com.example.demo.user.api.request.RoleQO;
import com.example.demo.user.api.request.RoleUpdateDTO;
import com.example.demo.user.biz.service.SysRoleMenuService;
import com.example.demo.user.biz.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author oyg
 * @Date 2018/8/18/18:16
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends AbstractController {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @GetMapping("/list")
    @PreAuthorize("@ps.hasPermission('sys:role:list')")
    public AirResult<PageUtils<SysRole>> list(RoleQO roleQO) {
        //如果不是超级管理员，则只查询自己创建的角色列表
        Long createUserId = null;
        if (getUserId() != Constant.SUPER_ADMIN) {
            createUserId = getUserId();
        }

        PageUtils page = sysRoleService.queryPage(roleQO, createUserId);

        return AirResult.ok(page);

    }

    /**
     * 角色列表
     *
     * @return
     */
    @GetMapping("/select")
    @PreAuthorize("@ps.hasPermission('sys:role:list')")
    public AirResult<List<SysRole>> select() {
        Map<String, Object> map = new HashMap<>();

        if (getUserId() != Constant.SUPER_ADMIN) {
            map.put("createUserId", getUserId());
        }
        Collection<SysRole> list = sysRoleService.listByMap(map);

        return AirResult.ok(list);
    }

    @GetMapping("/info/{roleId}")
    @PreAuthorize("@ps.hasPermission('sys:role:info')")
    public AirResult<SysRole> info(@PathVariable("roleId") Long roleId) {
        SysRole role = sysRoleService.getById(roleId);

        //查询角色对应的菜单
        List<Long> menuIdList = sysRoleMenuService.queryMenuIdList(roleId);
        role.setMenuIdList(menuIdList);

        return AirResult.ok(role);
    }

    @SysLogAn("保存角色")
    @PostMapping("/save")
    @PreAuthorize("@ps.hasPermission('sys:role:save')")
    public AirResult save(RequestEntity<RoleAddDTO> requestEntity) {
        RoleAddDTO roleAddDTO = requestEntity.getBody();
        ValidatorUtils.validateEntity(roleAddDTO);

        sysRoleService.add(roleAddDTO, getUserId());

        return AirResult.success();
    }

    @SysLogAn("删除角色")
    @DeleteMapping("/delete")
    @PreAuthorize("@ps.hasPermission('sys:role:delete')")
    public AirResult delete(@RequestBody Long[] roleIds) {
        sysRoleService.deleteBatch(roleIds);

        return AirResult.success();
    }

    @PostMapping("/test")
    public void li(@RequestParam String username, String password) {

        System.err.println(username);
    }

    @SysLogAn("修改角色")
    @PutMapping("/update")
    @PreAuthorize("@ps.hasPermission('sys:role:update')")
    public AirResult update(RequestEntity<RoleUpdateDTO> entity){
        RoleUpdateDTO sysRole = entity.getBody();
        ValidatorUtils.validateEntity(sysRole);

        sysRoleService.updateRole(sysRole);

        return AirResult.ok();
    }
}
