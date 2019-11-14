package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CategoryDao;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    // Request path: /menu
    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "My Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddCheeseForm(@ModelAttribute @Valid Menu newMenu,
                                       Errors errors,
                                       Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title","Add Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);
        int id = newMenu.getId();

        return "redirect:view/" + id;
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(Model model, @RequestParam int menuId) {

        Menu theMenu = new Menu();

        Optional<Menu> m = menuDao.findById(menuId);
        if(m.isPresent()) {
            theMenu = m.get();
        }

        List<Cheese> cheeses = theMenu.getCheeses();
        model.addAttribute("cheeses", cheeses);
        model.addAttribute("title", theMenu.getName());
        model.addAttribute("menuId", theMenu.getId());
        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int menuId) {

        Menu theMenu = new Menu();

        Optional<Menu> m = menuDao.findById(menuId);
        if(m.isPresent()) {
            theMenu = m.get();
        }

        AddMenuItemForm form = new AddMenuItemForm(cheeseDao.findAll(), theMenu);
        model.addAttribute("title", "Add item to menu: " + theMenu.getName());
        model.addAttribute("form", form);
        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String addItem(Model model, Errors errors,
                          @ModelAttribute @Valid AddMenuItemForm form) {

        if (errors.hasErrors()) {
            model.addAttribute("form", form);
            return "menu/add-item";
        }

        Cheese theCheese= new Cheese();
        Menu theMenu = new Menu();

        Optional<Cheese> c = cheeseDao.findById(form.getCheeseId());
        if(c.isPresent()) {
            theCheese = c.get();
        }
        Optional<Menu> m = menuDao.findById(form.getMenuId());
        if(m.isPresent()) {
            theMenu = m.get();
        }

        theMenu.addItem(theCheese);
        menuDao.save(theMenu);
        return "redirect:/menu/view/" + theMenu.getId();
    }
}