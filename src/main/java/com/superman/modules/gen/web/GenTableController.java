package com.superman.modules.gen.web;

import com.superman.common.persistence.Page;
import com.superman.common.utils.StringUtils;
import com.superman.common.web.BaseController;
import com.superman.modules.gen.entity.GenTable;
import com.superman.modules.gen.service.GenTableService;
import com.superman.modules.gen.util.GenUtils;
import com.superman.modules.sys.entity.User;
import com.superman.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Author: Super.Sun
 * Email: Super.Sun@163.com
 * <p>
 *  业务表Controller
 * CreateTime: 2016/7/18
 * Version: 1.0
 * Describe:
 */
@Controller
@RequestMapping(value = "${adminPath}/gen/genTable")
public class GenTableController  extends BaseController {

    @Autowired
    private GenTableService genTableService;

    @ModelAttribute
    public GenTable get(@RequestParam(required=false) String id) {
        if (StringUtils.isNotBlank(id)){
            return genTableService.get(id);
        }else{
            return new GenTable();
        }
    }

    @RequiresPermissions("gen:genTable:view")
    @RequestMapping(value = {"list", ""})
    public String list(GenTable genTable, HttpServletRequest request, HttpServletResponse response, Model model) {
        User user = UserUtils.getUser();
        if (!user.isAdmin()){
            genTable.setCreateBy(user);
        }
        Page<GenTable> page = genTableService.find(new Page<GenTable>(request, response), genTable);
        model.addAttribute("page", page);
        return "modules/gen/genTableList";
    }

    @RequiresPermissions("gen:genTable:view")
    @RequestMapping(value = "form")
    public String form(GenTable genTable, Model model) {
        // 获取物理表列表
        List<GenTable> tableList = genTableService.findTableListFormDb(new GenTable());
        model.addAttribute("tableList", tableList);
        // 验证表是否存在
        if (StringUtils.isBlank(genTable.getId()) && !genTableService.checkTableName(genTable.getName())){
            addMessage(model, "下一步失败！" + genTable.getName() + " 表已经添加！");
            genTable.setName("");
        }
        // 获取物理表字段
        else{
            genTable = genTableService.getTableFormDb(genTable);
        }
        model.addAttribute("genTable", genTable);
        model.addAttribute("config", GenUtils.getConfig());
        return "modules/gen/genTableForm";
    }

    @RequiresPermissions("gen:genTable:edit")
    @RequestMapping(value = "save")
    public String save(GenTable genTable, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, genTable)){
            return form(genTable, model);
        }
        // 验证表是否已经存在
        if (StringUtils.isBlank(genTable.getId()) && !genTableService.checkTableName(genTable.getName())){
            addMessage(model, "保存失败！" + genTable.getName() + " 表已经存在！");
            genTable.setName("");
            return form(genTable, model);
        }
        genTableService.save(genTable);
        addMessage(redirectAttributes, "保存业务表'" + genTable.getName() + "'成功");
        return "redirect:" + adminPath + "/gen/genTable/?repage";
    }

    @RequiresPermissions("gen:genTable:edit")
    @RequestMapping(value = "delete")
    public String delete(GenTable genTable, RedirectAttributes redirectAttributes) {
        genTableService.delete(genTable);
        addMessage(redirectAttributes, "删除业务表成功");
        return "redirect:" + adminPath + "/gen/genTable/?repage";
    }
}
