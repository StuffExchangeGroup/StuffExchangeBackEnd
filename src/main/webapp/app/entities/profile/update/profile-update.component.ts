import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IProfile, Profile } from '../profile.model';
import { ProfileService } from '../service/profile.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ICity } from 'app/entities/city/city.model';
import { CityService } from 'app/entities/city/service/city.service';
import { ILevel } from 'app/entities/level/level.model';
import { LevelService } from 'app/entities/level/service/level.service';

@Component({
  selector: 'jhi-profile-update',
  templateUrl: './profile-update.component.html',
})
export class ProfileUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  citiesSharedCollection: ICity[] = [];
  levelsSharedCollection: ILevel[] = [];

  editForm = this.fb.group({
    id: [],
    displayName: [],
    balance: [],
    latitude: [],
    longitude: [],
    avatar: [],
    phone: [],
    dob: [],
    location: [],
    countryCode: [],
    point: [],
    user: [],
    city: [],
    level: [],
  });

  constructor(
    protected profileService: ProfileService,
    protected userService: UserService,
    protected cityService: CityService,
    protected levelService: LevelService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ profile }) => {
      if (profile.id === undefined) {
        const today = dayjs().startOf('day');
        profile.dob = today;
      }

      this.updateForm(profile);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const profile = this.createFromForm();
    if (profile.id !== undefined) {
      this.subscribeToSaveResponse(this.profileService.update(profile));
    } else {
      this.subscribeToSaveResponse(this.profileService.create(profile));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  trackCityById(index: number, item: ICity): number {
    return item.id!;
  }

  trackLevelById(index: number, item: ILevel): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProfile>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(profile: IProfile): void {
    this.editForm.patchValue({
      id: profile.id,
      displayName: profile.displayName,
      balance: profile.balance,
      latitude: profile.latitude,
      longitude: profile.longitude,
      avatar: profile.avatar,
      phone: profile.phone,
      dob: profile.dob ? profile.dob.format(DATE_TIME_FORMAT) : null,
      location: profile.location,
      countryCode: profile.countryCode,
      point: profile.point,
      user: profile.user,
      city: profile.city,
      level: profile.level,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, profile.user);
    this.citiesSharedCollection = this.cityService.addCityToCollectionIfMissing(this.citiesSharedCollection, profile.city);
    this.levelsSharedCollection = this.levelService.addLevelToCollectionIfMissing(this.levelsSharedCollection, profile.level);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.cityService
      .query()
      .pipe(map((res: HttpResponse<ICity[]>) => res.body ?? []))
      .pipe(map((cities: ICity[]) => this.cityService.addCityToCollectionIfMissing(cities, this.editForm.get('city')!.value)))
      .subscribe((cities: ICity[]) => (this.citiesSharedCollection = cities));

    this.levelService
      .query()
      .pipe(map((res: HttpResponse<ILevel[]>) => res.body ?? []))
      .pipe(map((levels: ILevel[]) => this.levelService.addLevelToCollectionIfMissing(levels, this.editForm.get('level')!.value)))
      .subscribe((levels: ILevel[]) => (this.levelsSharedCollection = levels));
  }

  protected createFromForm(): IProfile {
    return {
      ...new Profile(),
      id: this.editForm.get(['id'])!.value,
      displayName: this.editForm.get(['displayName'])!.value,
      balance: this.editForm.get(['balance'])!.value,
      latitude: this.editForm.get(['latitude'])!.value,
      longitude: this.editForm.get(['longitude'])!.value,
      avatar: this.editForm.get(['avatar'])!.value,
      phone: this.editForm.get(['phone'])!.value,
      dob: this.editForm.get(['dob'])!.value ? dayjs(this.editForm.get(['dob'])!.value, DATE_TIME_FORMAT) : undefined,
      location: this.editForm.get(['location'])!.value,
      countryCode: this.editForm.get(['countryCode'])!.value,
      point: this.editForm.get(['point'])!.value,
      user: this.editForm.get(['user'])!.value,
      city: this.editForm.get(['city'])!.value,
      level: this.editForm.get(['level'])!.value,
    };
  }
}
