package cn.tycoding.system.controller.router;

import cn.tycoding.common.constants.CommonConstant;
import cn.tycoding.common.constants.SiteConstant;
import cn.tycoding.common.controller.BaseController;
import cn.tycoding.common.utils.QueryPage;
import cn.tycoding.system.entity.*;
import cn.tycoding.system.entity.dto.ArchivesWithArticle;
import cn.tycoding.system.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 博客前台路由控制层
 *
 * @author tycoding
 * @date 2019-09-10
 */
@Controller
public class SiteRouterController extends BaseController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private LinkService linkService;
    @Autowired
    private TagService tagService;

    @GetMapping("/error/500")
    public String error() {
        return "error/500";
    }

    /**
     * 初始化博客前台基础数据
     *
     * @param model
     */
    private void init(Model model) {
        //封装最新的8条文章数据
        List<SysArticle> articleList = articleService.findAll();
        articleList.forEach(a -> {
            a.setContent(null);
            a.setContentMd(null);
        });
        model.addAttribute(SiteConstant.RECENT_POSTS, articleList);

        //封装最新的8条评论数据
        List<SysComment> commentList = commentService.findAll();
        model.addAttribute(SiteConstant.RECENT_COMMENTS, commentList);
    }

    /**
     * 首页路由
     *
     * @return
     */
    @RequestMapping({"", "/", "/page/{p}"})
    public String index(@PathVariable(required = false) String p, Model model) {
        try {
            if (StringUtils.isBlank(p)) {
                p = "1";
            }
            IPage<SysArticle> page = new Page<>(Integer.valueOf(p), SiteConstant.DEFAULT_PAGE_LIMIT);
            LambdaQueryWrapper<SysArticle> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByDesc(SysArticle::getId);
            queryWrapper.eq(SysArticle::getState, CommonConstant.DEFAULT_RELEASE_STATUS);
            IPage<SysArticle> list = articleService.page(page, queryWrapper);
            list.getRecords().forEach(a -> {
                String content = Jsoup.parse(a.getContent()).text();
                if (content.length() > 50) {
                    content = content.substring(0, 40) + "...";
                }
                a.setContent(content);
                a.setContentMd(null);

                if (StringUtils.isNotBlank(a.getCategory())) {
                    SysCategory category = categoryService.getById(a.getCategory());
                    if (category != null) {
                        a.setCategory(category.getName());
                    } else {
                        a.setCategory(null);
                    }
                }
            });
            Map<String, Object> data = super.getData(list);
            data.put("current", list.getCurrent());
            data.put("pages", list.getPages());
            model.addAttribute(SiteConstant.INDEX_MODEL, data);

            //初始化
            this.init(model);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error/500";
        }
        return "site/index";
    }

    /**
     * 归档页面
     *
     * @return
     */
    @RequestMapping("/archives")
    public String archives(Model model) {
        try {
            List<ArchivesWithArticle> list = articleService.findArchives();


            //初始化
            this.init(model);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error/500";
        }
        return "site/page/archives";
    }

    /**
     * 友链页面
     *
     * @return
     */
    @RequestMapping("/links")
    public String links(@RequestParam(name = "page", required = false) String page, Model model) {
        try {
            if (page == null || page == "") {
                page = "1";
            }
            QueryPage queryPage = new QueryPage(Integer.valueOf(page), SiteConstant.COMMENT_PAGE_LIMIT);
            List<SysLink> list = linkService.list();
            model.addAttribute(SiteConstant.LINKS_MODEL, list);

            //封装评论数据
            Map<String, Object> map = commentService.findCommentsList(queryPage, null, SiteConstant.COMMENT_SORT_LINKS);
            model.addAttribute(SiteConstant.COMMENTS_MODEL, map);

            //初始化
            this.init(model);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error/500";
        }
        return "site/page/links";
    }

    /**
     * 关于我页面
     *
     * @return
     */
    @RequestMapping("/about")
    public String about(@RequestParam(name = "page", required = false) String page, Model model) {
        try {
            if (page == null || page == "") {
                page = "1";
            }
            QueryPage queryPage = new QueryPage(Integer.valueOf(page), SiteConstant.COMMENT_PAGE_LIMIT);
            //封装评论数据
            Map<String, Object> map = commentService.findCommentsList(queryPage, null, SiteConstant.COMMENT_SORT_ABOUT);
            model.addAttribute(SiteConstant.COMMENTS_MODEL, map);

            //初始化
            this.init(model);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error/500";
        }
        return "site/page/about";
    }
    @RequestMapping("/github")
    public String github(@PathVariable(required = false) String p, Model model) {
        try {
            common(p,model);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error/500";
        }
        return "front/github";
    }
    @RequestMapping("/message")
    public String message(@PathVariable(required = false) String p, Model model) {
        try {
            common(p,model);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error/500";
        }
        return "front/message";
    }
    @RequestMapping("/archive")
    public String archive(@PathVariable(required = false) String p, Model model) {
        try {
            common(p,model);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error/500";
        }
        return "front/archive";
    }
    @RequestMapping("/book")
    public String book(@PathVariable(required = false) String p, Model model) {

        try {
            common(p,model);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error/500";
        }
        return "front/book";
    }
    @RequestMapping({"/test","/test/{p}"})
    public String test(@PathVariable(required = false) String p, Model model) {
        try {
            common(p,model);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error/500";
        }
        return "front/index";
    }
    /**
     * 文章详情页路由
     *
     * @param id 文章的Id
     * @return
     */
    @RequestMapping("/article/{id}")
    public String article(@PathVariable String id, @RequestParam(name = "page", required = false) String page, Model model) {
        List<SysTag> tags =  tagService.findAll();
        Map<String, Object> map = new HashMap<>();
        List<SysCategory> categories =  categoryService.findAll();
        List<SysLink> links = linkService.findAll();
        if (id == null) {
            return this.error();
        }
        try {
            if (page == null || page == "") {
                page = "1";
            }
            QueryPage queryPage = new QueryPage(Integer.valueOf(page), SiteConstant.COMMENT_PAGE_LIMIT);
            if (StringUtils.isNotBlank(id)) {

                SysArticle article = articleService.findById(Long.valueOf(id));
                article.setSeeCount(article.getSeeCount()+1);
                articleService.update(article);
                if (article == null || article.getState().equals(CommonConstant.DEFAULT_DRAFT_STATUS)) {
                    return "redirect:/error/500";
                }
                model.addAttribute(SiteConstant.ARTICLE_MODEL, article);
                //封装该文章的评论数据
                Map<String, Object> data = commentService.findCommentsList(queryPage, id, SiteConstant.COMMENT_SORT_ARTICLE);
                data.put("tags",tags);
                data.put("map",map);
                model.addAttribute(SiteConstant.INDEX_MODEL, data);
            }

            //初始化
            this.init(model);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error/500";
        }
        return "front/detailArticle";
    }

    public void common(String p, Model model){
        List<ArchivesWithArticle> archives = articleService.findArchives();
        List<SysTag> tags =  tagService.findAll();
        Map<String, Object> map = new HashMap<>();
        List<SysCategory> categories =  categoryService.findAll();
        List<SysLink> links = linkService.findAll();
        for (SysCategory sysCategory : categories) {
            List<SysArticle> sysArticleList = articleService.findByCategory(sysCategory.getName());
            map.put(sysCategory.getName(), sysArticleList.size());
        }
        if (StringUtils.isBlank(p)) {
            p = "1";
        }
        IPage<SysArticle> page = new Page<>(Integer.valueOf(p), SiteConstant.DEFAULT_PAGE_LIMIT);
        LambdaQueryWrapper<SysArticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(SysArticle::getId);
        queryWrapper.eq(SysArticle::getState, CommonConstant.DEFAULT_RELEASE_STATUS);
        IPage<SysArticle> list = articleService.page(page, queryWrapper);
        list.getRecords().forEach(a -> {
            String content = Jsoup.parse(a.getContent()).text();
            if (content.length() > 50) {
                content = content.substring(0, 40) + "...";
            }
            a.setContent(content);
            a.setContentMd(null);

            if (StringUtils.isNotBlank(a.getCategory())) {
                SysCategory category = categoryService.getById(a.getCategory());
                if (category != null) {
                    a.setCategory(category.getName());
                } else {
                    a.setCategory(null);
                }
            }
        });
        Map<String, Object> data = super.getData(list);
        data.put("tags",tags);
        data.put("map",map);
        data.put("current", list.getCurrent());
        data.put("pages", list.getPages());
        data.put("links",links);
        data.put("archives",archives);
        model.addAttribute(SiteConstant.INDEX_MODEL, data);
        //初始化
        this.init(model);
    }
}