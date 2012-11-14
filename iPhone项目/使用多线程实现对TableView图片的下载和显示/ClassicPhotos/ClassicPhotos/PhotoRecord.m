//
//  PhotoRecord.m
//  ClassicPhotos
//
//  Created by l l on 16/09/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import "PhotoRecord.h"

@implementation PhotoRecord

@synthesize name = _name,image = _image,URL = _URL,hasImage = _hasImage,filtered =_filtered,failed = _failed;

- (BOOL)hasImage {
    return _image != nil;
}


- (BOOL)isFailed {
    return _failed;
}


- (BOOL)isFiltered {
    return _filtered;
}

@end
