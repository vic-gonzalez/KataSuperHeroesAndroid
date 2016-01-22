/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karumi.katasuperheroes;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import com.karumi.katasuperheroes.di.MainComponent;
import com.karumi.katasuperheroes.di.MainModule;
import com.karumi.katasuperheroes.model.SuperHero;
import com.karumi.katasuperheroes.model.SuperHeroesRepository;
import com.karumi.katasuperheroes.ui.view.MainActivity;
import it.cosenonjaviste.daggermock.DaggerMockRule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.karumi.katasuperheroes.matchers.RecyclerViewItemsCountMatcher.recyclerViewHasItemCount;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class) @LargeTest public class MainActivityTest {

  private static final String EMPTY_CASE_MESSAGE = "¯\\_(ツ)_/¯";
  private static final int HEROES_MAX_MOCKED_COUNT = 12;
  private static final int FIRST_POSITION = 0;

  @Rule public DaggerMockRule<MainComponent> daggerRule =
      new DaggerMockRule<>(MainComponent.class, new MainModule()).set(
          new DaggerMockRule.ComponentSetter<MainComponent>() {
            @Override
            public void setComponent(MainComponent component) {
              SuperHeroesApplication app =
                  (SuperHeroesApplication) InstrumentationRegistry.getInstrumentation()
                      .getTargetContext()
                      .getApplicationContext();
              app.setComponent(component);
            }
          });

  @Rule public IntentsTestRule<MainActivity> activityRule =
      new IntentsTestRule<>(MainActivity.class, true, false);

  @Mock SuperHeroesRepository repository;

  @Test
  public void showsEmptyCaseIfThereAreNoSuperHeroes() {
    givenThereAreNoSuperHeroes();

    startActivity();

    onView(withText(EMPTY_CASE_MESSAGE)).check(matches(isDisplayed()));
  }

  @Test
  public void hideEmptyCaseMessageIfThereAreSuperHeroes() {
    givenThereAreSuperHeroes();

    startActivity();

    onView(withText(EMPTY_CASE_MESSAGE)).check(matches(not(isDisplayed())));
  }

  @Test
  public void shouldSeeOneHeroeWhenIHaveOneHeroe() {
    givenThereAreSuperHeroes(1);

    startActivity();

    onView(withId(R.id.recycler_view)).check(matches(recyclerViewHasItemCount(1)));
  }

  @Test
  public void shouldSeeMaxHeroesCountWhenIHaveThem() {
    givenThereAreSuperHeroes();

    startActivity();

    onView(withId(R.id.recycler_view)).check(
        matches(recyclerViewHasItemCount(HEROES_MAX_MOCKED_COUNT)));
  }

  @Test
  public void shouldSeeAllSuperheroesNamesOnMyList() {
    givenThereAreSuperHeroes();

    startActivity();

    List<SuperHero> fullHeroesList = getMockedHeroesList();
    for (SuperHero superHero : fullHeroesList) {
      onView(withId(R.id.recycler_view)).perform(
          RecyclerViewActions.scrollTo(hasDescendant(withText(superHero.getName()))))
          .check(matches(hasDescendant(withText(superHero.getName()))));
    }
  }

  private void givenThereAreNoSuperHeroes() {
    when(repository.getAll()).thenReturn(Collections.<SuperHero>emptyList());
  }

  private void givenThereAreSuperHeroes() {
    givenThereAreSuperHeroes(HEROES_MAX_MOCKED_COUNT);
  }

  private void givenThereAreSuperHeroes(int count) {
    if (count < 1 || count > 12) {
      throw new IllegalArgumentException("Superherores count must be between 1 and 12");
    }
    when(repository.getAll()).thenReturn(getMockedHeroesList().subList(0, count));
  }

  private List<SuperHero> getMockedHeroesList() {
    List<SuperHero> heroesList = new ArrayList<>();

    heroesList.add(new SuperHero("Scarlet Witch",
        "https://i.annihil.us/u/prod/marvel/i/mg/9/b0/537bc2375dfb9.jpg", false,
        "Scarlet Witch was born at the Wundagore base of the High Evolutionary, she and her twin "
            + "brother Pietro were the children of Romani couple Django and Marya Maximoff. The "
            + "High Evolutionary supposedly abducted the twins when they were babies and "
            + "experimented on them, once he was disgusted with the results, he returned them to"
            + " Wundagore, disguised as regular mutants."));
    heroesList.add(
        new SuperHero("Iron Man", "https://i.annihil.us/u/prod/marvel/i/mg/c/60/55b6a28ef24fa.jpg",
            true, "Wounded, captured and forced to build a weapon by his enemies, billionaire "
            + "industrialist Tony Stark instead created an advanced suit of armor to save his "
            + "life and escape captivity. Now with a new outlook on life, Tony uses his money "
            + "and intelligence to make the world a safer, better place as Iron Man."));
    heroesList.add(
        new SuperHero("Wolverine", "https://i.annihil.us/u/prod/marvel/i/mg/9/00/537bcb1133fd7.jpg",
            false,
            "Born with super-human senses and the power to heal from almost any wound, Wolverine "
                + "was captured by a secret Canadian organization and given an unbreakable "
                + "skeleton and claws. Treated like an animal, it took years for him to control"
                + " himself. Now, he's a premiere member of both the X-Men and the Avengers."));
    heroesList.add(
        new SuperHero("Hulk", "https://x.annihil.us/u/prod/marvel/i/mg/e/e0/537bafa34baa9.jpg",
            true,
            "Caught in a gamma bomb explosion while trying to save the life of a teenager, Dr. "
                + "Bruce Banner was transformed into the incredibly powerful creature called the "
                + "Hulk. An all too often misunderstood hero, the angrier the Hulk gets, the "
                + "stronger the Hulk gets."));
    heroesList.add(
        new SuperHero("Storm", "https://x.annihil.us/u/prod/marvel/i/mg/c/b0/537bc5f8a8df0.jpg",
            false,
            "Ororo Monroe is the descendant of an ancient line of African priestesses, all of whom"
                + " have white hair, blue eyes, and the potential to wield magic."));
    heroesList.add(new SuperHero("Spider-Man",
        "https://x.annihil.us/u/prod/marvel/i/mg/6/60/538cd3628a05e.jpg", true,
        "Bitten by a radioactive spider, high school student Peter Parker gained the speed, "
            + "strength and powers of a spider. Adopting the name Spider-Man, Peter hoped to start "
            + "a career using his new abilities. Taught that with great power comes great "
            + "responsibility, Spidey has vowed to use his powers to help people."));
    heroesList.add(
        new SuperHero("Ultron", "https://i.annihil.us/u/prod/marvel/i/mg/9/a0/537bc7f6d5d23.jpg",
            false,
            "Arguably the greatest and certainly the most horrific creation of scientific genius "
                + "Dr. Henry Pym, Ultron is a criminally insane rogue sentient robot dedicated to"
                + " conquest and the extermination of humanity."));
    heroesList.add(new SuperHero("BlackPanther",
        "https://i.annihil.us/u/prod/marvel/i/mg/9/03/537ba26276348.jpg", false,
        " T'Challa is a brilliant tactician, strategist, scientist, tracker and a master of all "
            + "forms of unarmed combat whose unique hybrid fighting style incorporates acrobatics "
            + "and aspects of animal mimicry. T'Challa being a royal descendent of a warrior race "
            + "is also a master of armed combat, able to use a variety of weapons but prefers "
            + "unarmed combat. He is a master planner who always thinks several steps ahead and "
            + "will go to extreme measures to achieve his goals and protect the kingdom "
            + "of Wakanda."));
    heroesList.add(new SuperHero("Captain America",
        "http://x.annihil.us/u/prod/marvel/i/mg/9/80/537ba5b368b7d.jpg", true,
        "Captain America represented the pinnacle of human physical perfection. He experienced a "
            + "time when he was augmented to superhuman levels, but generally performed just below"
            + " superhuman levels for most of his career. Captain America had a very high "
            + "intelligence as well as agility, strength, speed, endurance, and reaction time "
            + "superior to any Olympic athlete who ever competed."));
    heroesList.add(new SuperHero("Winter Soldier",
        "https://i.annihil.us/u/prod/marvel/i/mg/7/40/537bca868687c.jpg", false,
        "Olympic-class athlete and exceptional acrobat highly skilled in both unarmed and armed "
            + "hand-to-hand combat and extremely accurate marksman. he is fluent in four languages "
            + "including German and Russian."));
    heroesList.add(new SuperHero("Captain Marvel",
        "https://x.annihil.us/u/prod/marvel/i/mg/6/30/537ba61b764b4.jpg", false,
        " Ms. Marvel's current powers include flight, enhanced strength, durability and the "
            + "ability to shoot concussive energy bursts from her hands."));
    heroesList.add(
        new SuperHero("Iron Fist", "https://i.annihil.us/u/prod/marvel/i/mg/6/60/537bb1756cd26.jpg",
            false,
            "Through concentration, Iron Fist can harness his spiritual energy, or chi, to augment "
                + "his physical and mental capabilities to peak human levels. By focusing his chi "
                + "into his hand, he can tap the superhuman energy of Shou-Lao and temporarily "
                + "render his fist superhumanly powerful, immune to pain and injury; however, this "
                + "process is mentally draining, and he usually needs recovery time before he can "
                + "repeat it. Iron Fist can heal himself of any injury or illness and project this "
                + "power to heal others."));

    return heroesList;
  }

  private MainActivity startActivity() {
    return activityRule.launchActivity(null);
  }
}