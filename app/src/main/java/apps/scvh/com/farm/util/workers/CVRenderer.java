package apps.scvh.com.farm.util.workers;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jrummyapps.android.util.HtmlBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;

import apps.scvh.com.farm.R;
import apps.scvh.com.farm.util.cv.CV;
import apps.scvh.com.farm.util.di.DaggerAppComponent;
import apps.scvh.com.farm.util.di.ObjectProvider;
import apps.scvh.com.farm.util.enums.CVFields;
import apps.scvh.com.farm.util.enums.TextTypes;

/**
 * ENG class used for making html
 * RUS класс которым я генерю хтмлку
 */
public class CVRenderer extends AsyncTask<CV, Integer, String> {

    private Context context;
    private ProgressDialog bar;

    @Inject
    @Named("RendererHelper")
    RenderHelper renderHelper;

    public CVRenderer(Context context) {
        bar = new ProgressDialog(context);
        this.context = context;
        DaggerAppComponent.builder().objectProvider(new
                ObjectProvider(context)).build().inject(this);
    }

    private String renderCV(CV cv) {
        HtmlBuilder builder = new HtmlBuilder();
        builder.append(context.getString(R.string.head_open));
        builder.append(context.getString(R.string.utf_encoding));
        builder.append(context.getString(R.string.style_open));
        setFonts(builder);
        setGravity(builder);
        builder.append(context.getString(R.string.style_close));
        builder.append(context.getString(R.string.head_close));
        builder.append(context.getString(R.string.body_open));
        builder.append(context.getString(R.string.name_div));
        builder.h1(cv.getFullName());
        builder.append(context.getString(R.string.div_close));
        builder.append(context.getString(R.string.about_div));
        builder.h2(cv.getAbout());
        builder.append(context.getString(R.string.div_close));
        try {
            drawList(builder, cv.getEducation(), CVFields.EDUCATION);
            drawList(builder, cv.getExperience(), CVFields.EXPERIENCE);
            drawList(builder, cv.getLinks(), CVFields.LINKS);
            drawList(builder, cv.getProjects(), CVFields.PROJECTS);
            drawList(builder, cv.getPrimarySkills(), CVFields.PRIMARY_SKILLS);
            drawList(builder, cv.getSecondarySkills(), CVFields.SECONDARY_SKILLS);
            drawList(builder, cv.getOtherSkills(), CVFields.OTHER_SKILLS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        builder.append(context.getString(R.string.body_close));
        return builder.toString(); //i'm not building it, i just want html
    }

    private void drawList(HtmlBuilder builder, ArrayList<String> list, CVFields flag)
            throws IOException {
        if (!list.isEmpty()) {
            builder.append(renderHelper.getDivWithClassForField(flag));
            Iterator<String> iterator = list.iterator();
            builder.h1(renderHelper.getStringForField(flag));
            while (iterator.hasNext()) {
                builder.h2(iterator.next());
            }
            builder.append(context.getString(R.string.div_close));
        } else {
            Log.d(context.getString(R.string.pdf_render_debug), context.getString(R.string
                    .pdf_render_null));
        }
    }

    private void setFonts(HtmlBuilder builder) {
        builder.append(String.format(context.getString(R.string.header_font_size),
                renderHelper.getFontSize(TextTypes.BIG_TEXT)));
        builder.append(String.format(context.getString(R.string.text_font_size),
                renderHelper.getFontSize(TextTypes.SMALL_TEXT)));
    }

    private void setGravity(HtmlBuilder builder) {
        builder.append(String.format(context.getString(R.string.name_css), renderHelper
                .getGravity(CVFields.NAME)));
        builder.append(String.format(context.getString(R.string.about_css), renderHelper
                .getGravity(CVFields.ABOUT)));
        builder.append(String.format(context.getString(R.string.education_css), renderHelper
                .getGravity(CVFields.EDUCATION)));
        builder.append(String.format(context.getString(R.string.experience_css), renderHelper
                .getGravity(CVFields.EXPERIENCE)));
        builder.append(String.format(context.getString(R.string.links_css), renderHelper
                .getGravity(CVFields.LINKS)));
        builder.append(String.format(context.getString(R.string.projects_css), renderHelper
                .getGravity(CVFields.PROJECTS)));
        builder.append(String.format(context.getString(R.string.primary_skills_css), renderHelper
                .getGravity(CVFields.PRIMARY_SKILLS)));
        builder.append(String.format(context.getString(R.string.secondary_skills_css), renderHelper
                .getGravity(CVFields.SECONDARY_SKILLS)));
        builder.append(String.format(context.getString(R.string.other_skills_css), renderHelper
                .getGravity(CVFields.OTHER_SKILLS)));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        bar.show();
        bar.setTitle(context.getString(R.string.loading));
        bar.setMessage(context.getString(R.string.please_wait));
    }

    @Override
    protected String doInBackground(CV... params) {
        return renderCV(params[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        bar.dismiss();
    }

}
